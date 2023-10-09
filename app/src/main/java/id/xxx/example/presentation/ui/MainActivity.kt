package id.xxx.example.presentation.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import id.xxx.example.R
import id.xxx.example.presentation.ui.a.AFragment

class MainActivity : FragmentActivity() {

    companion object {

        val PERMISSIONS = arrayOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_VIDEO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        )
    }

    private val activityResultLauncherMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var isGranted = false
            for (permission in PERMISSIONS) {
                isGranted = permissions[permission] ?: false
            }
            if (isGranted) init() else finishAfterTransition()
        }

    private fun init() {
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_browse_fragment, AFragment())
            .commitNow()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityResultLauncherMultiplePermissions.launch(PERMISSIONS)
    }
}