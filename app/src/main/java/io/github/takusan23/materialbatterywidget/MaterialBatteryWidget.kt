package io.github.takusan23.materialbatterywidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.SizeF
import android.view.View
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
                // 標準
                val remoteView = RemoteViews(context.packageName, R.layout.widget_material_battery).apply {
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
                    // smallView のときの Visibility を引き継ぐらしいので消す
                    setViewVisibility(R.id.material_battery_widget_device_name_textview, View.VISIBLE)
                    setViewVisibility(R.id.material_battery_widget_device_battery_textview, View.VISIBLE)
                    setViewVisibility(R.id.material_battery_widget_bluetooth_name_textview, View.VISIBLE)
                    setViewVisibility(R.id.material_battery_widget_bluetooth_battery_textview, View.VISIBLE)
                    // 押したら再読み込みするようにBroadcastを設定する
                    val pendingIntent = PendingIntent.getBroadcast(
                        context, PENDING_INTENT_REQUEST_CODE, Intent(context, MaterialBatteryWidget::class.java),
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    setOnClickPendingIntent(R.id.material_battery_widget_parent, pendingIntent)
                }
                // レスポンシブデザインにする
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // 小さいウィジェット
                    val smallView = RemoteViews(remoteView).apply {
                        setViewVisibility(R.id.material_battery_widget_device_name_textview, View.GONE)
                        setViewVisibility(R.id.material_battery_widget_device_battery_textview, View.GONE)
                        setViewVisibility(R.id.material_battery_widget_bluetooth_name_textview, View.GONE)
                        setViewVisibility(R.id.material_battery_widget_bluetooth_battery_textview, View.GONE)
                    }
                    // 横に長いウィジェット
                    val mediumView = RemoteViews(remoteView).apply {
                        setViewVisibility(R.id.material_battery_widget_device_name_textview, View.GONE)
                        setViewVisibility(R.id.material_battery_widget_device_battery_textview, View.VISIBLE)
                        setViewVisibility(R.id.material_battery_widget_bluetooth_name_textview, View.GONE)
                        setViewVisibility(R.id.material_battery_widget_bluetooth_battery_textview, View.VISIBLE)
                    }
                    // レスポンシブ
                    val viewMapping = mapOf(
                        // width <= 2 , height = 1
                        SizeF(100f, 100f) to smallView,
                        // width >= 2 , height = 1
                        SizeF(200f, 100f) to mediumView,
                        // width >= 2 , height >= 2
                        SizeF(200f, 200f) to remoteView
                    )
                    val remoteViews = RemoteViews(viewMapping)
                    appWidgetManager.updateAppWidget(id, remoteViews)
                } else {
                    // 更新
                    appWidgetManager.updateAppWidget(id, remoteView)
                }
            }
        }
    }
}