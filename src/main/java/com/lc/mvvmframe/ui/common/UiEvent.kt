package com.lc.mvvmframe.ui.common

import com.lc.mvvmframe.domain.error.AppError

sealed class UiEvent {
    data class ShowError(val error: AppError, val retryActionId: String? = null) : UiEvent()
    data class ShowToast(val message: String) : UiEvent()
}

