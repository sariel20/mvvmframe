package com.lc.mvvmframe.network

import com.lc.mvvmframe.domain.error.AppError
import okhttp3.internal.http2.StreamResetException
import retrofit2.HttpException
import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import kotlinx.coroutines.CancellationException

/**
 * 把网络/解析等异常统一映射为 AppError。
 *
 * 说明：
 * - 这里不依赖 Android Context，避免在 VM/Repo 层难以复用与测试。
 * - 文案尽量通用，业务可在上层按需要覆写。
 */
object NetworkErrorMapper {
    fun fromThrowable(t: Throwable): AppError {
        // 协程取消不应提示为“错误”
        if (t is CancellationException) {
            return AppError(
                type = AppError.Type.Canceled,
                message = "已取消",
                cause = t,
                isRetryable = false,
            )
        }

        val root = unwrap(t)

        return when (root) {
            is UnknownHostException -> AppError(
                type = AppError.Type.NetworkUnavailable,
                message = "网络不可用，请检查网络设置",
                cause = t,
                isRetryable = true,
            )
            is SocketTimeoutException -> AppError(
                type = AppError.Type.Timeout,
                message = "请求超时，请稍后重试",
                cause = t,
                isRetryable = true,
            )
            is ConnectException -> AppError(
                type = AppError.Type.NetworkUnavailable,
                message = "连接失败，请检查网络",
                cause = t,
                isRetryable = true,
            )
            is SSLException -> AppError(
                type = AppError.Type.NetworkUnavailable,
                message = "安全连接失败，请稍后重试",
                cause = t,
                isRetryable = true,
            )
            is StreamResetException -> AppError(
                type = AppError.Type.NetworkUnavailable,
                message = "连接中断，请稍后重试",
                cause = t,
                isRetryable = true,
            )
            is EOFException -> AppError(
                type = AppError.Type.Parse,
                message = "数据解析失败",
                cause = t,
                isRetryable = true,
            )
            is HttpException -> fromHttpException(root, original = t)
            is IOException -> AppError(
                type = AppError.Type.NetworkUnavailable,
                message = "网络异常，请稍后重试",
                cause = t,
                isRetryable = true,
            )
            else -> AppError(
                type = AppError.Type.Unknown,
                message = root.message?.takeIf { it.isNotBlank() } ?: "未知错误",
                cause = t,
                isRetryable = false,
            )
        }
    }

    fun business(code: Int?, message: String?, cause: Throwable? = null): AppError {
        return AppError(
            type = AppError.Type.Business,
            message = message?.takeIf { it.isNotBlank() } ?: "请求失败",
            code = code,
            cause = cause,
            isRetryable = true,
            requiresAuth = code == 401,
        )
    }

    private fun fromHttpException(e: HttpException, original: Throwable): AppError {
        val code = e.code()
        return when (code) {
            401 -> AppError(
                type = AppError.Type.Unauthorized,
                message = "登录已失效，请重新登录",
                code = code,
                cause = original,
                isRetryable = false,
                requiresAuth = true,
            )
            403 -> AppError(
                type = AppError.Type.Forbidden,
                message = "无权限访问",
                code = code,
                cause = original,
                isRetryable = false,
            )
            404 -> AppError(
                type = AppError.Type.NotFound,
                message = "资源不存在",
                code = code,
                cause = original,
                isRetryable = true,
            )
            in 500..599 -> AppError(
                type = AppError.Type.Server,
                message = "服务器异常，请稍后重试",
                code = code,
                cause = original,
                isRetryable = true,
            )
            else -> AppError(
                type = AppError.Type.Http,
                message = "请求失败（$code）",
                code = code,
                cause = original,
                isRetryable = code in 408..499,
            )
        }
    }

    private fun unwrap(t: Throwable): Throwable {
        // Retrofit/协程链路里常见“外层包装”，这里做一层剥离
        return when (t) {
            is HttpException -> t
            else -> t.cause ?: t
        }
    }
}

