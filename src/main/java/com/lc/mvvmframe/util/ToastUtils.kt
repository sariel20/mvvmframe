package com.lc.mvvmframe.util

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat

/**
 * Toast 工具类
 *
 * 解决 Toast 重复显示、切换语言后显示旧语言等问题
 *
 * @author Cheng
 */
object ToastUtils {

    private var toast: Toast? = null

    /**
     * 显示短 Toast
     *
     * @param context 上下文
     * @param message 消息内容
     */
    fun showShort(context: Context, message: String) {
        show(context, message, Toast.LENGTH_SHORT)
    }

    /**
     * 显示长 Toast
     *
     * @param context 上下文
     * @param message 消息内容
     */
    fun showLong(context: Context, message: String) {
        show(context, message, Toast.LENGTH_LONG)
    }

    @JvmOverloads
    fun show(message: String, duration: Int = Toast.LENGTH_SHORT) {
        val context = com.lc.mvvmframe.MvvmFrameApp.getInstance() ?: return
        show(context, message, duration)
    }

    private fun show(context: Context, message: String, duration: Int) {
        // 取消之前的 Toast
        toast?.cancel()

        // 创建新的 Toast
        toast = Toast.makeText(context, message, duration).apply {
            setText(message)
        }
        toast?.show()
    }

    /**
     * 取消 Toast 显示
     */
    fun cancel() {
        toast?.cancel()
    }
}
