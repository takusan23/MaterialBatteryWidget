package io.github.takusan23.materialbatterywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 */
class MaterialBatteryWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateAllAppWidget(context)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * ブロードキャスト受け取り
     * */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null) {
            updateAllAppWidget(context)
        }
    }

    companion object {

        /** PendingIntentのリクエスト番号 */
        private val PENDING_INTENT_REQUEST_CODE = 1919

        /**
         * すべてのウイジェットを更新する
         *
         * @param context [Context]
         * */
        fun updateAllAppWidget(context: Context) {
            val appWidgetManager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
            val component = ComponentName(context, MaterialBatteryWidget::class.java)
            val widgetIdList = appWidgetManager.getAppWidgetIds(component)
            widgetIdList.forEach { id ->
                val view = RemoteViews(context.packageName, R.layout.widget_material_battery).apply {
                    // 電池残量を取得
                    val smartphoneBatteryData = BatteryUtil.getDeviceBatteryData(context)
                    val bluetoothBatteryData = BatteryUtil.getBluetoothBatteryData(context)
                    // スマホの残量
                    setTextViewText(R.id.material_battery_widget_device_name_textview, smartphoneBatteryData.name)
                    setTextViewText(R.id.material_battery_widget_device_battery_textview, "${smartphoneBatteryData.level}%")
                    setProgressBar(R.id.material_battery_widget_device_progressbar, 100, smartphoneBatteryData.level, false)
                    // Bluetoothデバイスの残量。ペアリングしてない場合はnullになる
                    setTextViewText(R.id.material_battery_widget_bluetooth_name_textview, bluetoothBatteryData?.name ?: context.getString(R.string.disconnected))
                    setTextViewText(R.id.material_battery_widget_bluetooth_battery_textview, bluetoothBatteryData?.level?.let { "$it%" } ?: "")
                    setProgressBar(R.id.material_battery_widget_bluetooth_progressbar, 100, bluetoothBatteryData?.level ?: 0, false)
                    // 押したら再読み込みするようにBroadcastを設定する
                    val pendingIntent = PendingIntent.getBroadcast(context, PENDING_INTENT_REQUEST_CODE, Intent(context, MaterialBatteryWidget::class.java),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
                    setOnClickPendingIntent(R.id.material_battery_widget_parent, pendingIntent)
                }
                // 更新
                appWidgetManager.updateAppWidget(id, view)
            }
        }
    }
}