package com.lc.mvvmframe.network

import com.lc.mvvmframe.core.session.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 统一处理 401：清理登录态并发出会话失效事件。
 *
 * 注意：这里不做 refresh token 逻辑（项目目前没有对应 API/流程）。
 */
@Singleton
class SessionAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
) : Authenticator {

    private val fired = AtomicBoolean(false)

    override fun authenticate(route: Route?, response: Response): Request? {
        // 避免同一轮请求链路中重复触发
        if (!fired.compareAndSet(false, true)) return null

        sessionManager.onSessionExpired()
        // 返回 null 表示不重试该请求
        return null
    }
}

