package com.lc.mvvmframe.util

import android.content.Context
import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation

/**
 * 常用扩展函数
 *
 * @author Cheng
 */

// ==================== Context 扩展 ====================

/**
 * dp 转 px
 */
fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

/**
 * px 转 dp
 */
fun Context.px2dp(px: Int): Float {
    return px / resources.displayMetrics.density
}

// ==================== String 扩展 ====================

/**
 * 判断字符串是否为空
 */
fun String?.isNullOrEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * 判断字符串是否为空或空白
 */
fun String?.isNullOrBlank(): Boolean {
    return this.isNullOrBlank()
}

/**
 * 如果字符串为空，返回默认值
 */
fun String?.orDefault(default: String = ""): String {
    return this ?: default
}

// ==================== View 扩展 ====================

/**
 * 设置 View 可见性为 VISIBLE
 */
fun android.view.View.show() {
    visibility = android.view.View.VISIBLE
}

/**
 * 设置 View 可见性为 GONE
 */
fun android.view.View.hide() {
    visibility = android.view.View.GONE
}

/**
 * 设置 View 可见性为 INVISIBLE
 */
fun android.view.View.invisible() {
    visibility = android.view.View.INVISIBLE
}

/**
 * 根据条件设置可见性
 */
fun android.view.View.showIf(condition: Boolean) {
    visibility = if (condition) android.view.View.VISIBLE else android.view.View.GONE
}

// ==================== ImageView 扩展 ====================

/**
 * 加载图片（普通）
 */
fun ImageView.loadImage(url: String?, placeholder: Int? = null) {
    load(url) {
        placeholder?.let { placeholder(it) }
        crossfade(true)
    }
}

/**
 * 加载圆形图片
 */
fun ImageView.loadCircleImage(url: String?, placeholder: Int? = null) {
    load(url) {
        placeholder?.let { placeholder(it) }
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

/**
 * 加载本地图片
 */
fun ImageView.loadResource(resId: Int) {
    load(resId)
}

// ==================== Collection 扩展 ====================

/**
 * 判断列表是否为空
 */
fun <T> List<T>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

/**
 * 如果列表为空，返回默认值
 */
fun <T> List<T>?.orEmpty(): List<T> {
    return this ?: emptyList()
}
