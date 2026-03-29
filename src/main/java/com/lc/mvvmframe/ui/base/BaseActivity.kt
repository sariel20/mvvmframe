package com.lc.mvvmframe.ui.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.lc.mvvmframe.core.session.SessionEvent
import com.lc.mvvmframe.core.session.SessionManager
import com.lc.mvvmframe.di.SessionEntryPoint
import com.lc.mvvmframe.ui.common.Event
import com.lc.mvvmframe.ui.common.UiEvent
import com.lc.mvvmframe.ui.widget.StateLayout
import dagger.hilt.android.EntryPointAccessors

/**
 * 基础 Activity
 *
 * 提供：
 * - ViewBinding 自动绑定
 * - ViewModel 自动创建和关联
 * - 加载状态、空状态、错误状态 UI 管理
 * - 生命周期管理
 *
 * 使用方式：
 * 1. 继承 BaseActivity
 * 2. 指定 VB 类型（ViewBinding）和 VM 类型（ViewModel）
 * 3. 实现抽象方法
 *
 * @param VB ViewBinding 类型
 * @param VM ViewModel 类型
 * @author 小龙虾
 */
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel<IBaseView>> : AppCompatActivity(), IBaseView {

    /**
     * ViewBinding 实例
     * 在 onCreate 中初始化
     */
    protected lateinit var binding: VB

    /**
     * ViewModel 实例
     * 优先使用注入的 ViewModel，否则通过 createViewModel() 创建
     */
    protected val viewModel: VM by lazy {
        injectedViewModel ?: createViewModel()
    }

    /**
     * 注入的 ViewModel（可选）
     * 如果子类通过 @Inject 注入了 ViewModel，设置此属性
     */
    protected var injectedViewModel: VM? = null

    /**
     * 加载中 View
     * 子类可自定义实现
     */
    protected var loadingView: View? = null

    /**
     * 空状态 View
     * 子类可自定义实现
     */
    protected var emptyView: View? = null

    /**
     * 内容 View
     * 用于切换显示/隐藏
     */
    protected var contentViewRef: View? = null

    /**
     * 可选：使用 StateLayout 承载多状态页面
     */
    protected var stateLayout: StateLayout? = null

    /**
     * 创建 ViewBinding
     *
     * @return ViewBinding 实例
     */
    abstract fun createBinding(): VB

    /**
     * 创建 ViewModel
     * 如果子类通过 @Inject 注入了 ViewModel，此方法可选择返回 injectedViewModel
     *
     * @return ViewModel 实例
     */
    open fun createViewModel(): VM {
        return injectedViewModel!!
    }

    /**
     * 初始化视图
     *
     * 在此处：
     * - 初始化 ViewBinding
     * - 设置布局
     * - 初始化 Views
     * - 绑定 ViewModel
     */
    abstract fun initView()

    /**
     * 初始化数据
     *
     * 在 onCreate 末尾调用
     * 用于：
     * - 加载初始数据
     * - 观察 LiveData
     */
    abstract fun initData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建 ViewBinding
        binding = createBinding()
        setContentView(binding.root)

        // 关联 ViewModel
        viewModel.attachView(this)

        // 初始化视图
        initView()

        // 初始化数据
        initData()

        // 观察 ViewModel 状态
        observeViewModel()

        // 观察会话事件（如 401 导致的登录失效）
        observeSessionEvents()
    }

    protected open fun onSessionExpired() {
        // 默认不做导航，由业务层自行处理（比如跳转登录页、清空栈等）
    }

    private fun observeSessionEvents() {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            SessionEntryPoint::class.java
        )
        val sessionManager: SessionManager = entryPoint.sessionManager()
        sessionManager.events.observe(this) { event: Event<SessionEvent> ->
            when (event.getContentIfNotHandled()) {
                SessionEvent.SessionExpired -> onSessionExpired()
                null -> Unit
            }
        }
    }

    /**
     * 观察 ViewModel 的 LiveData
     *
     * 可在子类重写添加更多观察
     */
    protected open fun observeViewModel() {
        // 观察加载状态
        viewModel.loading.observe(this) { isLoading ->
           if (isLoading) {
            showLoading()
           } else {
            hideLoading()
           }
        }

        // 观察错误信息
        viewModel.error.observe(this) { message ->
            message?.let { showError(it) }
        }

        // 观察一次性 UI 事件（包含重试回调）
        viewModel.uiEvents.observe(this) { event: Event<UiEvent> ->
            when (val e = event.getContentIfNotHandled()) {
                is UiEvent.ShowError -> {
                    val hasRetry = e.retryActionId != null
                    Snackbar.make(binding.root, e.error.message, Snackbar.LENGTH_LONG)
                        .setAction(if (hasRetry) "重试" else "关闭") {
                            e.retryActionId?.let { viewModel.runAction(it) }
                        }
                        .show()
                }
                is UiEvent.ShowToast -> showToast(e.message)
                null -> Unit
            }
        }

        // 观察空状态
        viewModel.empty.observe(this) { message ->
            if (message != null) {
                showEmpty(message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.detachView()
    }

    // ==================== IBaseView 实现 ====================

    override fun showLoading(message: String?) {
        stateLayout?.showLoading() ?: run {
            loadingView?.visibility = View.VISIBLE
            contentViewRef?.visibility = View.GONE
            emptyView?.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        stateLayout?.showContent() ?: run {
            loadingView?.visibility = View.GONE
            // 根据是否有空状态显示内容或空状态
            if (emptyView?.visibility == View.VISIBLE) {
                contentViewRef?.visibility = View.GONE
            } else {
                contentViewRef?.visibility = View.VISIBLE
            }
        }
    }

    override fun showError(message: String, retry: Boolean) {
        // 默认使用 Snackbar 显示错误
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction(if (retry) "重试" else "关闭") {
                if (retry) {
                    // 重试逻辑由子类实现
                }
            }
            .show()
    }

    override fun showEmpty(message: String, icon: Int?) {
        stateLayout?.showEmpty(message) ?: run {
            emptyView?.visibility = View.VISIBLE
            contentViewRef?.visibility = View.GONE
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // ==================== 快捷方法 ====================

    /**
     * 设置加载中 View
     */
    protected fun setLoadingViewRef(view: View) {
        this.loadingView = view
    }

    /**
     * 设置空状态 View
     */
    protected fun setEmptyViewRef(view: View) {
        this.emptyView = view
    }

    /**
     * 设置内容 View 引用
     */
    protected fun bindContentView(view: View) {
        this.contentViewRef = view
    }

    /**
     * 绑定 StateLayout（推荐）。绑定后 showLoading/showEmpty/showError 会优先走 StateLayout。
     */
    protected fun bindStateLayout(layout: StateLayout, onRetry: (() -> Unit)? = null) {
        stateLayout = layout
        stateLayout?.setOnRetry(onRetry)
    }
}
