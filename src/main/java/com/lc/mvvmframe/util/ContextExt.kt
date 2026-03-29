package com.lc.mvvmframe.util

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Context 扩展函数
 */

/**
 * 将 dp 转换为 px
 */
fun Context.dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

/**
 * 在 Fragment 中快速显示 Toast
 */
fun Fragment.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), msg, duration).show()
}

/**
 * 在 Context 中快速显示 Toast
 */
fun Context.showToast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}
