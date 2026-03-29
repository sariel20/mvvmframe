package com.lc.mvvmframe.core.crash

import timber.log.Timber

object CrashHandler {
    @Volatile
    private var installed = false

    @Volatile
    var reporter: CrashReporter? = null

    fun install() {
        if (installed) return
        installed = true

        val prev = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            try {
                Timber.e(e, "Uncaught exception in thread=${t.name}")
                reporter?.report(e)
            } catch (_: Throwable) {
                // ignore
            } finally {
                prev?.uncaughtException(t, e)
            }
        }
    }
}

