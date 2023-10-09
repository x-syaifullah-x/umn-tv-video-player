package id.xxx.example.presentation.ui.a

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import id.xxx.example.R
import id.xxx.example.presentation.ui.b.BFragment
import id.xxx.example.presentation.ui.b.BVideo
import id.xxx.example.presentation.ui.c.CFragment

class AFragment : Fragment(R.layout.a_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.bb, BFragment::class.java, null)
            .commitNow()
    }

    fun play(video: BVideo) {
        val args = Bundle()
        args.putParcelable(CFragment.KEY_URI_DATA, video.uri)
        childFragmentManager.beginTransaction()
            .replace(R.id.cc, CFragment::class.java, args)
            .commit()
    }
}