package com.lc.mvvmframe.core.session

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lc.mvvmframe.data.local.sp.PreferencesManager
import com.lc.mvvmframe.ui.common.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val preferencesManager: PreferencesManager,
) {
    private val _events = MutableLiveData<Event<SessionEvent>>()
    val events: LiveData<Event<SessionEvent>> = _events

    fun onSessionExpired() {
        preferencesManager.clearLoginData()
        _events.postValue(Event(SessionEvent.SessionExpired))
    }
}

