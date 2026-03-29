package com.lc.mvvmframe.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lc.mvvmframe.domain.error.AppError
import com.lc.mvvmframe.domain.model.Resource
import com.lc.mvvmframe.network.NetworkErrorMapper
import com.lc.mvvmframe.ui.common.Event
import com.lc.mvvmframe.ui.common.UiEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 基础 ViewModel
 *
 * 提供：
 * - 加载状态管理（LiveData）
 * - 协程异常处理
 * - 通用错误处理
 *
 * @param V View 类型
 * @author 小龙虾
 */
abstract class BaseViewModel<V : IBaseView> : ViewModel() {

    /**
     * 视图引用
     * 在 Activity/Fragment 关联后设置
     */
    protected var view: V? = null

    /**
     * 加载状态
     * true - 显示加载中，false - 隐藏加载中
     */
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    /**
     * 错误信息
     * 用于显示错误 Toast 或 Dialog
     */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    /**
     * 结构化错误（更适合做“可重试/需要登录”等决策）
     */
    private val _appError = MutableLiveData<AppError>()
    val appError: LiveData<AppError> = _appError

    /**
     * 一次性 UI 事件（Toast、可重试错误等）
     */
    private val _uiEvents = MutableLiveData<Event<UiEvent>>()
    val uiEvents: LiveData<Event<UiEvent>> = _uiEvents

    private val actionRegistry = ConcurrentHashMap<String, () -> Unit>()

    /**
     * 空状态
     * 用于显示空状态 UI
     */
    private val _empty = MutableLiveData<String?>()
    val empty: LiveData<String?> = _empty

    /**
     * 全局协程异常处理器
     *
     * 统一处理协程中的异常，避免崩溃
     */
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        handleException(throwable)
    }

    /**
     * 关联视图
     *
     * 在 Activity 的 onCreate 或 Fragment 的 onViewCreated 中调用
     *
     * @param view 视图实例
     */
    fun attachView(view: V) {
        this.view = view
    }

    /**
     * 解除视图关联
     *
     * 在 Activity 的 onDestroy 或 Fragment 的 onDestroyView 中调用
     */
    fun detachView() {
        this.view = null
    }

    /**
     * 显示加载中
     */
    protected fun showLoading() {
        _loading.postValue(true)
    }

    /**
     * 隐藏加载中
     */
    protected fun hideLoading() {
        _loading.postValue(false)
    }

    /**
     * 显示错误
     *
     * @param message 错误信息
     */
    protected fun showError(message: String) {
        _error.postValue(message)
        hideLoading()
    }

    /**
     * 显示空状态
     *
     * @param message 空状态信息
     */
    protected fun showEmpty(message: String) {
        _empty.postValue(message)
        hideLoading()
    }

    /**
     * 清除空状态
     */
    protected fun clearEmpty() {
        _empty.postValue(null)
    }

    /**
     * 处理异常
     *
     * @param throwable 异常对象
     */
    protected open fun handleException(throwable: Throwable) {
        val mapped = NetworkErrorMapper.fromThrowable(throwable)
        _appError.postValue(mapped)
        _uiEvents.postValue(Event(UiEvent.ShowError(mapped)))
        showError(mapped.message)
    }

    /**
     * 发送一个带“重试动作”的错误事件。
     * Activity/Fragment 统一消费并把按钮点击回调到这里注册的 action。
     */
    protected fun emitRetryableError(error: AppError, retry: () -> Unit) {
        val id = UUID.randomUUID().toString()
        actionRegistry[id] = retry
        _appError.postValue(error)
        _uiEvents.postValue(Event(UiEvent.ShowError(error, retryActionId = id)))
        showError(error.message)
    }

    fun runAction(actionId: String) {
        val action = actionRegistry.remove(actionId) ?: return
        action.invoke()
    }

    /**
     * 启动协程的快捷方法
     *
     * 自动处理：
     * - 显示/隐藏 loading
     * - 异常处理
     *
     * @param showLoading 是否显示加载中（默认 true）
     * @param block 协程执行体
     */
    protected fun launch(
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.launch(exceptionHandler) {
            if (showLoading) showLoading()
            try {
                block()
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * 处理 Resource 结果
     *
     * 简化 ViewModel 中对网络请求结果的处理
     *
     * @param result Resource 结果
     * @param onSuccess 成功回调
     * @param onError 错误回调（可选）
     * @param onLoading 加载中回调（可选）
     */
    protected fun <T> handleResult(
        result: Resource<T>,
        onSuccess: (T) -> Unit,
        onError: ((String) -> Unit)? = null,
        onLoading: (() -> Unit)? = null
    ) {
        when (result) {
            is Resource.Loading -> {
                onLoading?.invoke()
                showLoading()
            }
            is Resource.Success -> {
                hideLoading()
                clearEmpty()
                onSuccess(result.data)
            }
            is Resource.Error -> {
                hideLoading()
                val mapped = result.exception?.let { NetworkErrorMapper.fromThrowable(it) }
                if (mapped != null) {
                    _appError.postValue(mapped)
                    _uiEvents.postValue(Event(UiEvent.ShowError(mapped)))
                    onError?.invoke(mapped.message)
                    showError(mapped.message)
                } else {
                    onError?.invoke(result.message)
                    showError(result.message)
                }
            }
        }
    }
}
