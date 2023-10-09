package id.xxx.example.presentation.ui.c

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import id.xxx.example.R

class CFragment : Fragment(R.layout.c_fragment) {

    companion object {
        const val KEY_URI_DATA = "KEY_URI_DATA"
    }

    private var exoPlayer: ExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        requireActivity().requestWindowFeature(Window.FEATURE_NO_TITLE)
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val argumentsFinal = arguments
        if (argumentsFinal != null) {
            val playerView = view.findViewById<PlayerView>(R.id.video_view)
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                argumentsFinal.getParcelable(KEY_URI_DATA, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                argumentsFinal.getParcelable(KEY_URI_DATA)
            } ?: throw NullPointerException()
            initializePlayer(playerView, uri)
//            initializePlayer(playerView, "https://v2.siar.us/aditv/livestream/chunks.m3u8".toUri())
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer(view: PlayerView, url: Uri) {
        exoPlayer = ExoPlayer.Builder(view.context).build()
        view.player = exoPlayer
        val mediaItem =
            MediaItem.Builder()
                .setUri(url)
                .build()
        if (url.lastPathSegment?.contains("m3u") == true) {
            val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSource = HlsMediaSource.Factory(defaultHttpDataSourceFactory)
                .createMediaSource(mediaItem)
            exoPlayer?.setMediaSource(mediaSource)
        } else {
            exoPlayer?.setMediaItem(mediaItem)
        }
        exoPlayer?.playWhenReady = true
        exoPlayer?.prepare()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
}