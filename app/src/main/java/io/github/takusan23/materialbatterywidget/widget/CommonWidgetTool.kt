package io.github.takusan23.materialbatterywidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import io.github.takusan23.materialbatterywidget.BatteryUtil
import io.github.takusan23.materialbatterywidget.R

/**  [MaterialBatteryWidget] / [MaterialBatteryWidgetVertical] の共通処理 */
object CommonWidgetTool {

    /** PendingIntent のリクエスト番号 */
    private const val PENDING_INTENT_REQUEST_CODE = 1919

    /**
     * [MaterialBatteryWidget] / [MaterialBatteryWidgetVertical] をすべて更新する
     *
     * @param context [Context]
     */
    fun updateAllWidget(context: Context) {
        val appWidgetManager = context.getSystemService(Context.APPWIDGET_SERVICE) as AppWidgetManager
        // MaterialBatteryWidget を更新
        ComponentName(context, MaterialBatteryWidget::class.java).also { component ->
            // ウィジェットを全部更新
            appWidgetManager.getAppWidgetIds(component).forEach { id ->
                appWidgetManager.updateAppWidget(
                    id, createCommonWidgetLayout(
                        context = context,
                        layoutId = R.layout.widget_material_battery,
                        broadcastReceiverClass = MaterialBatteryWidget::class.java
                    )
                )
            }
        }
        // MaterialBatteryWidgetVertical を更新
        ComponentName(context, MaterialBatteryWidgetVertical::class.java).also { component ->
            // ウィジェットを全部更新
            appWidgetManager.getAppWidgetIds(component).forEach { id ->
                appWidgetManager.updateAppWidget(
                    id, createCommonWidgetLayout(
                        context = context,
                        layoutId = R.layout.widget_material_battery_vertical,
                        broadcastReceiverClass = MaterialBatteryWidgetVertical::class.java
                    )
                )
            }
        }
    }


    /**
     * ウィジェットのレイアウトを生成する
     * [MaterialBatteryWidget] / [MaterialBatteryWidgetVertical] で共通して使う
     *
     * @param context [Context]
     * @param layoutId ウィジェットのレイアウトID
     * @param broadcastReceiverClass ブロードキャスト送信先のクラス
     * @return [RemoteViews]
     */
    private fun createCommonWidgetLayout(context: Context, layoutId: Int, broadcastReceiverClass: Class<*>): RemoteViews {
        // 標準
        val remoteView = RemoteViews(context.packageName, layoutId).apply {
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
            // smallView / mediumView のときの Visibility を引き継ぐらしいので消す
            setViewVisibility(R.id.material_battery_widget_device_name_textview, View.VISIBLE)
            setViewVisibility(R.id.material_battery_widget_device_battery_textview, View.VISIBLE)
            setViewVisibility(R.id.material_battery_widget_bluetooth_name_textview, View.VISIBLE)
            setViewVisibility(R.id.material_battery_widget_bluetooth_battery_textview, View.VISIBLE)
            // 押したら再読み込みするようにBroadcastを設定する
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                PENDING_INTENT_REQUEST_CODE,
                Intent(context, broadcastReceiverClass),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )
            setOnClickPendingIntent(R.id.material_battery_widget_parent, pendingIntent)
        }

        // Android 12 以降の場合はレスポンシブデザインにする
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
            RemoteViews(viewMapping)
        } else {
            remoteView
        }
    }
}