package id.xxx.example.presentation.ui.b

import android.annotation.SuppressLint
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.xxx.example.R
import id.xxx.example.databinding.BItemBinding
import java.util.Locale
import java.util.concurrent.TimeUnit

class BRecyclerViewAdapter(
    private val onItemClick: (BVideo) -> Unit = {}
) : RecyclerView.Adapter<BRecyclerViewAdapterViewHolder>() {

    private val items: MutableList<BVideo> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BRecyclerViewAdapterViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.b_item, parent, false)
        return BRecyclerViewAdapterViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BRecyclerViewAdapterViewHolder, position: Int) {
        val item = items[position]
        val view = holder.itemView
        val viewBinding = BItemBinding.bind(view)

        viewBinding.root.setOnClickListener { onItemClick(item) }

        // Load thumbnail of a specific media item.
        println(item.uri)
        val thumbnail =
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val cs = CancellationSignal()
                    view.context.contentResolver?.loadThumbnail(
                        item.uri, Size(120, 80), cs
                    )
                } else {
                    @Suppress("DEPRECATION") MediaStore.Images.Thumbnails.getThumbnail(
                        view.context.contentResolver,
                        item.uri.lastPathSegment?.toLong() ?: 0,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                    )
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        viewBinding.imageViewThumbnail.setImageBitmap(thumbnail)

        viewBinding.textViewName.text = item.name

        viewBinding.textViewDuration.text = getDurationString(item.duration.toLong())
    }

    private fun getDurationString(durationMs: Long, negativePrefix: Boolean = false): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)

        return if (hours > 0) {
            String.format(
                Locale.getDefault(),
                "%s%02d:%02d:%02d",
                if (negativePrefix) "- " else "",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
        } else String.format(
            Locale.getDefault(),
            "%s%02d:%02d",
            if (negativePrefix) "- " else "",
            minutes,
            seconds - TimeUnit.MINUTES.toSeconds(minutes)
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataChanged(data: MutableList<BVideo>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }
}