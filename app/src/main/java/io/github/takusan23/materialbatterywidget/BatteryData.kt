package io.github.takusan23.materialbatterywidget

/**
 * 名前と電池残量のデータクラス
 *
 * @param name 名前
 * @param level 電池残量
 * */
data class BatteryData(
    val name: String,
    val level: Int,
)