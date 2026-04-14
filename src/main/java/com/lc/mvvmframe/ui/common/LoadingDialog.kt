package com.lc.mvvmframe.ui.common

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lc.mvvmframe.databinding.DialogLoadingBinding
import java.lang.ref.WeakReference

/**
 * 加载中弹窗
 *
 * 用于显示请求等待时的加载状态
 * 使用 LifecycleObserver 自动管理生命周期，避免内存泄漏
 *
 * @author Cheng
 */
class LoadingDialog(context: Context) : LifecycleEventObserver {

    private val dialog: Dialog = Dialog(context)
    private val binding: DialogLoadingBinding
    private var lifecycleOwner: LifecycleOwner? = null

    init {
        binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        // 设置背景透明，实现圆角效果
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false) // 不可取消
        dialog.setCanceledOnTouchOutside(false)
    }

    /**
     * 绑定生命周期，自动销毁
     */
    fun bindLifecycle(owner: LifecycleOwner): LoadingDialog {
        this.lifecycleOwner = owner
        owner.lifecycle.addObserver(this)
        return this
    }

    /**
     * 设置加载提示文字
     */
    fun setMessage(message: String): LoadingDialog {
        binding.tvMessage.text = message
        return this
    }

    /**
     * 显示弹窗
     */
    fun show() {
        if (!dialog.isShowing) {
            try {
                dialog.show()
            } catch (e: Exception) {
                // 避免 Activity 已销毁时崩溃
            }
        }
    }

    /**
     * 隐藏弹窗
     */
    fun hide() {
        if (dialog.isShowing) {
            try {
                dialog.hide()
            } catch (e: Exception) {
                // 避免 Activity 已销毁时崩溃
            }
        }
    }

    /**
     * 关闭弹窗
     */
    fun dismiss() {
        try {
            dialog.dismiss()
        } catch (e: Exception) {
            // 避免 Activity 已销毁时崩溃
        }
    }

    /**
     * 判断是否正在显示
     */
    fun isShowing(): Boolean = dialog.isShowing

    /**
     * LifecycleEventObserver 实现
     * 在生命周期结束时自动销毁
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                dismiss()
                source.lifecycle.removeObserver(this)
                lifecycleOwner = null
            }
            else -> {}
        }
    }

    companion object {
        // 使用弱引用避免内存泄漏
        @Volatile
        private var weakInstance: WeakReference<LoadingDialog>? = null

        /**
         * 获取单例实例
         */
        fun getInstance(context: Context): LoadingDialog {
            weakInstance?.get()?.let { return it }

            synchronized(this) {
                weakInstance?.get()?.let { return it }

                val instance = LoadingDialog(context.applicationContext)
                weakInstance = WeakReference(instance)
                return instance
            }
        }

        /**
         * 显示加载弹窗
         */
        fun show(context: Context, message: String = "加载中...") {
            getInstance(context).setMessage(message).show()
        }

        /**
         * 隐藏加载弹窗
         */
        fun hide(context: Context) {
            getInstance(context).hide()
        }

        /**
         * 关闭加载弹窗
         */
        fun dismiss() {
            weakInstance?.get()?.dismiss()
            weakInstance = null
        }
    }
}
