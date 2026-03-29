package com.lc.mvvmframe.core.session

sealed class SessionEvent {
    data object SessionExpired : SessionEvent()
}

