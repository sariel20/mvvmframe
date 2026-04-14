package com.lc.mvvmframe.di

import com.lc.mvvmframe.data.repository.UserRepositoryImpl
import com.lc.mvvmframe.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 仓库模块
 *
 * 使用 @Binds 注解绑定接口到实现类
 * Hilt 会自动注入 UserRepositoryImpl 到 UserRepository
 *
 * @author Cheng
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 绑定 UserRepository 接口到实现类
     *
     * @param userRepositoryImpl UserRepository 实现类
     * @return UserRepository 接口
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
