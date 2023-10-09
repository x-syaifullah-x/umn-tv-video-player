package id.xxx.example.presentation.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import id.xxx.example.presentation.ui.c.CFragment

class VideoPlayer : FragmentActivity() {

    private val activityResultLauncherMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isGranted = false
            for (permission in MainActivity.PERMISSIONS) {
                isGranted = permissions[permission] ?: false
            }
            if (isGranted) {
                val uri = intent.data
                val bundle = bundleOf(CFragment.KEY_URI_DATA to uri)
                supportFragmentManager.beginTransaction()
                    .replace(android.R.id.content, CFragment::class.java, bundle, null)
                    .commit()
            } else {
                finishAfterTransition()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncherMultiplePermissions.launch(MainActivity.PERMISSIONS)
    }
}