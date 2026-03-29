package com.lc.mvvmframe.ui.common

/**
 * LiveData 一次性事件包装，避免旋转/重建时重复消费。
 */
class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        if (hasBeenHandled) return null
        hasBeenHandled = true
        return content
    }

    fun peekContent(): T = content
}

