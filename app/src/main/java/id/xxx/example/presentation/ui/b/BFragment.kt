package id.xxx.example.presentation.ui.b

import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.xxx.example.R
import id.xxx.example.databinding.BFragmentBinding
import id.xxx.example.presentation.ui.a.AFragment
import kotlinx.coroutines.launch

class BFragment : Fragment(R.layout.b_fragment) {

    companion object {
        private const val MESSAGE_NO_ITEM_VIDEO =
            "Sorry, no videos found on your device. Please add videos to your device or download from a suitable source to start enjoying video playback."
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = BFragmentBinding.bind(view)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

        val adapter = BRecyclerViewAdapter(
            onItemClick = { video ->
                (parentFragment as AFragment).play(video)
            }
        )
        recyclerView.adapter = adapter
        lifecycleScope.launch {
            getItems(
                requireContext(),
                collection = collection,
                onLoop = { index, count, video ->
                    if (index == 0) {
                        (parentFragment as AFragment).play(video)
                    }
                },
                onComplete = { results ->
                    binding.progressBar.isVisible = false
                    if (results.isEmpty()) {
                        binding.textViewNoItem.isVisible = true
                        binding.textViewNoItem.text = MESSAGE_NO_ITEM_VIDEO
                    } else {
                        adapter.notifyDataChanged(results)
                    }
                }
            )
        }

        val handler = Handler(Looper.getMainLooper())
        view.context.contentResolver.registerContentObserver(
            collection,
            true,
            object : ContentObserver(handler) {
                override fun onChange(selfChange: Boolean) {
                    getItems(
                        requireContext(),
                        collection = collection,
                        onComplete = { results ->
                            adapter.notifyDataChanged(results)
                            if (results.isEmpty()) {
                                binding.textViewNoItem.isVisible = true
                                binding.textViewNoItem.text = MESSAGE_NO_ITEM_VIDEO
                            } else {
                                binding.textViewNoItem.isVisible = false
                            }
                        }
                    )
                }
            })
    }

    private fun getItems(
        context: Context,
        collection: Uri,
        onLoop: (index: Int, count: Int, video: BVideo) -> Unit = { _, _, _ -> },
        onComplete: (MutableList<BVideo>) -> Unit = {}
    ) {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.ALBUM
        )

//        val selection = "${MediaStore.Video.Media.DURATION} >= ?"
//        val selectionArgs = arrayOf(
//            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//        )

// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"
        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        val results = mutableListOf<BVideo>()
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)


            var index = 0
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val album = cursor.getString(albumColumn)

                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )

                val video = BVideo(contentUri, name, duration, size)
                onLoop(index, cursor.count, video)
                index++
                results += video
            }
        }
        onComplete.invoke(results)
    }
}