package com.lc.mvvmframe.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

/**
 * 轻量多状态容器：Content / Loading / Empty / Error，并支持统一重试。
 *
 * 约定：第一个 child 视为 content（也可手动调用 setContentView）。
 */
class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private var contentView: View? = null
    private var retry: (() -> Unit)? = null

    private val loadingView: View by lazy {
        FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            addView(ProgressBar(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }
            })
        }
    }

    private fun messageView(message: String, showRetry: Boolean): View {
        return FrameLayout(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            val tv = TextView(context).apply {
                text = message
                gravity = Gravity.CENTER_HORIZONTAL
            }
            val btn = Button(context).apply {
                text = "重试"
                visibility = if (showRetry) View.VISIBLE else View.GONE
                setOnClickListener { retry?.invoke() }
            }
            addView(FrameLayout(context).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }
                addView(tv)
                addView(btn.apply {
                    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        topMargin = 24
                        gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    }
                })
            })
        }
    }

    private var emptyView: View? = null
    private var errorView: View? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 0 && contentView == null) {
            contentView = getChildAt(0)
        }
    }

    fun setContentView(view: View) {
        contentView = view
    }

    fun setOnRetry(action: (() -> Unit)?) {
        retry = action
    }

    fun showContent() {
        removeOverlay()
        contentView?.visibility = View.VISIBLE
    }

    fun showLoading() {
        showOverlay(loadingView)
        contentView?.visibility = View.GONE
    }

    fun showEmpty(message: String = "暂无数据") {
        emptyView = messageView(message, showRetry = false)
        showOverlay(emptyView!!)
        contentView?.visibility = View.GONE
    }

    fun showError(message: String = "加载失败", retryable: Boolean = true) {
        errorView = messageView(message, showRetry = retryable)
        showOverlay(errorView!!)
        contentView?.visibility = View.GONE
    }

    private fun showOverlay(view: View) {
        removeOverlay()
        addView(view)
    }

    private fun removeOverlay() {
        emptyView?.let { removeView(it) }
        errorView?.let { removeView(it) }
        if (loadingView.parent === this) removeView(loadingView)
        emptyView = null
        errorView = null
    }
}

