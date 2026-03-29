package com.lc.mvvmframe.core.crash

/**
 * 崩溃上报抽象：库内不强绑定具体平台（Crashlytics/Sentry），由 app 层按需接入。
 */
fun interface CrashReporter {
    fun report(throwable: Throwable)
}

