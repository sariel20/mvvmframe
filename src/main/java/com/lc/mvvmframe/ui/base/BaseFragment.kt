package com.lc.mvvmframe.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleEventObserver
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference

/**
 * 基础 Fragment
 *
 * 提供：
 * - ViewBinding 自动绑定
 * - ViewModel 自动创建和关联
 * - 加载状态、空状态、错误状态 UI 管理
 * - 生命周期管理
 *
 * 使用方式：
 * 1. 继承 BaseFragment
 * 2. 指定 VB 类型（ViewBinding）和 VM 类型（ViewModel）
 * 3. 实现抽象方法
 *
 * @param VB ViewBinding 类型
 * @param VM ViewModel 类型
 * @author 小龙虾
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel<IBaseView>> : Fragment(), IBaseView {

    /**
     * ViewBinding 实例
     * 使用弱引用避免内存泄漏
     */
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

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
     * 是否已初始化
     */
    protected var isInitialized = false

    /**
     * 加载中 View
     */
    protected var loadingView: View? = null

    /**
     * 空状态 View
     */
    protected var emptyView: View? = null

    /**
     * 内容 View
     */
    protected var contentViewRef: View? = null

    /**
     * 创建 ViewBinding
     *
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @return ViewBinding 实例
     */
    abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    /**
     * 创建 ViewModel
     * 如果子类通过 @Inject 注入了 ViewModel，此方法可选择返回 injectedViewModel
     *
     * @return ViewModel 实例
     */
    open fun createViewModel(): VM {
        return injectedViewModel ?: throw IllegalStateException("ViewModel not injected")
    }

    /**
     * 初始化视图
     *
     * 在 onViewCreated 中调用
     */
    abstract fun initView()

    /**
     * 初始化数据
     *
     * 在 initView 之后调用
     */
    abstract fun initData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 创建 ViewBinding
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 关联 ViewModel
        viewModel.attachView(this)

        // 初始化视图
        initView()

        // 初始化数据
        initData()

        // 标记已初始化
        isInitialized = true

        // 观察 ViewModel 状态
        observeViewModel()
    }

    /**
     * 观察 ViewModel 的 LiveData
     */
    protected open fun observeViewModel() {
        // 观察加载状态
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }

        // 观察错误信息
        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { showError(it) }
        }

        // 观察空状态
        viewModel.empty.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                showEmpty(message)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.detachView()
        // 清理 ViewBinding 引用
        _binding = null
    }

    // ==================== IBaseView 实现 ====================

    override fun showLoading(message: String?) {
        loadingView?.visibility = View.VISIBLE
        contentViewRef?.visibility = View.GONE
        emptyView?.visibility = View.GONE
    }

    override fun hideLoading() {
        loadingView?.visibility = View.GONE
        // 根据是否有空状态显示内容或空状态
        if (emptyView?.visibility == View.VISIBLE) {
            contentViewRef?.visibility = View.GONE
        } else {
            contentViewRef?.visibility = View.VISIBLE
        }
    }

    override fun showError(message: String, retry: Boolean) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction(if (retry) "重试" else "关闭") {
                    if (retry) {
                        // 重试逻辑由子类实现
                    }
                }
                .show()
        }
    }

    override fun showEmpty(message: String, icon: Int?) {
        emptyView?.visibility = View.VISIBLE
        contentViewRef?.visibility = View.GONE
    }

    override fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
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
     * 设置内容 View
     */
    protected fun bindContentView(view: View) {
        this.contentViewRef = view
    }
}
