package io.github.takusan23.materialbatterywidget

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build


object BatteryUtil {

    /**
     * スマホの名前と電池残量を取得する
     *
     * @param context [Context]
     * @return [BatteryData]
     * */
    fun getDeviceBatteryData(context: Context): BatteryData {
        val deviceName = Build.MODEL
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
            context.registerReceiver(null, intentFilter)
        }
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        return BatteryData(deviceName, status)
    }

    /**
     * Bluetoothペアリングデバイスの名前と電池残量を取得する
     *
     * @param context [Context]
     * @return [BatteryData]。存在しない場合はnull
     * */
    fun getBluetoothBatteryData(context: Context): BatteryData? {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (!bluetoothManager.adapter.isEnabled) return null

        // 電池残量を取得するメソッドが @hide で隠されているので、リフレクションで呼び出す
        val getBatteryLevelMethod = BluetoothDevice::class.java
            .methods
            .find { it.name == "getBatteryLevel" }!!
        val deviceList = bluetoothManager.adapter.bondedDevices
            .map { BatteryData(it.name, getBatteryLevelMethod.invoke(it) as Int) }
            .filter { it.level != -1 }

        return deviceList.firstOrNull()
    }

}