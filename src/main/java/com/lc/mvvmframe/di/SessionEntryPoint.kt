package com.lc.mvvmframe.di

import com.lc.mvvmframe.core.session.SessionManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SessionEntryPoint {
    fun sessionManager(): SessionManager
}

