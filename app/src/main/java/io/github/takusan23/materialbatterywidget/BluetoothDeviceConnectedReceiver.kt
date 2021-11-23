package io.github.takusan23.materialbatterywidget

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed

/**
 * Bluetoothペアリング済みデバイスの接続、切断ブロードキャストを取得する
 * */
class BluetoothDeviceConnectedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // 更新
        if (context != null) {
            // なんか遅延させないと取得できないっぽい？
            val delay = if (intent?.action == BluetoothDevice.ACTION_ACL_CONNECTED) 2_000L else 0L

            Handler(Looper.getMainLooper()).postDelayed(delay) {
                MaterialBatteryWidget.updateAllAppWidget(context)
            }
        }
    }

}