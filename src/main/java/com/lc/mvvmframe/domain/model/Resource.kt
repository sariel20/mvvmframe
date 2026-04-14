package com.lc.mvvmframe.domain.model

/**
 * 统一资源封装类，用于包装网络请求或本地数据的结果
 *
 * 使用 sealed class 确保类型安全，提供三种状态：
 * - Success: 请求成功，包含数据
 * - Error: 请求失败，包含错误信息和可选的异常
 * - Loading: 请求进行中
 *
 * @param T 数据类型
 * @author Cheng
 */
sealed class Resource<out T> {
    /**
     * 成功状态
     * @param data 成功返回的数据
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * 错误状态
     * @param message 错误信息描述
     * @param exception 可选的异常对象，用于调试
     */
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()

    /**
     * 加载中状态
     * 用于显示 loading UI
     */
    data object Loading : Resource<Nothing>()

    /**
     * 判断是否为成功状态
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * 判断是否为错误状态
     */
    val isError: Boolean get() = this is Error

    /**
     * 判断是否为加载中状态
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * 获取成功时的数据，如果当前不是成功状态返回 null
     */
    fun getOrNull(): T? = (this as? Success)?.data

    /**
     * 获取成功时的数据，如果当前不是成功状态返回默认值
     */
    fun getOrDefault(default: @UnsafeVariance T): T = getOrNull() ?: default
}
