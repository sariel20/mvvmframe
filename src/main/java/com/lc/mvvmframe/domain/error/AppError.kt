package com.lc.mvvmframe.domain.error

/**
 * 框架内部统一错误模型。
 *
 * 目标：在 UI/VM/Repository/Network 之间传递结构化错误信息，
 * 避免直接使用 Throwable.message 导致信息不一致、不可重试策略缺失等问题。
 */
data class AppError(
    val type: Type,
    val message: String,
    val code: Int? = null,
    val cause: Throwable? = null,
    val isRetryable: Boolean = false,
    val requiresAuth: Boolean = false,
) {
    enum class Type {
        NetworkUnavailable,
        Timeout,
        Http,
        Unauthorized,
        Forbidden,
        NotFound,
        Server,
        Parse,
        Canceled,
        Business,
        Unknown,
    }
}

