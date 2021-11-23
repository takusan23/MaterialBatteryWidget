package io.github.takusan23.materialbatterywidget

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import io.github.takusan23.materialbatterywidget.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /** ViewBinding */
    private val viewBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    /** GitHub URL */
    private val GITHUB_URL = "https://github.com/takusan23/MaterialBatteryWidget"

    /** 権限コールバック */
    private val permissionCallback = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        setPermissionRequestTextOrDescriptionText()
        viewBinding.button.isVisible = !it
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setPermissionRequestTextOrDescriptionText()

        // 権限リクエストボタン
        if (!isGrantedPermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            viewBinding.button.setOnClickListener {
                permissionCallback.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            viewBinding.button.isVisible = false
        }

        // ライセンスボタン
        viewBinding.activityMainLicenseButton.setOnClickListener {
            startActivity(Intent(this, LicenseActivity::class.java))
        }

        // ソースコード見るボタン
        viewBinding.activityMainSourceCodeButton.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, GITHUB_URL.toUri()))
        }

    }

    /** 権限下さいテキスト or アプリの説明 テキストをセットする */
    private fun setPermissionRequestTextOrDescriptionText() {
        viewBinding.activityMainTextview.text = if (isGrantedPermission()) {
            getString(R.string.app_description)
        } else {
            getString(R.string.permission_description)
        }
    }

    /** 権限があればtrueを返す */
    private fun isGrantedPermission() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    } else {
        // Android 12 以前はそもそも権限がないのでtrue
        true
    }

}