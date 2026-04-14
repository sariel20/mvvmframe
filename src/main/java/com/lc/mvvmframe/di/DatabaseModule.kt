package com.lc.mvvmframe.di

import android.content.Context
import androidx.room.Room
import com.lc.mvvmframe.data.local.db.AppDatabase
import com.lc.mvvmframe.data.local.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块
 *
 * 提供数据库相关的依赖注入配置：
 * - AppDatabase
 * - UserDao
 *
 * @author Cheng
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供 AppDatabase 实例
     *
     * 使用 Room.databaseBuilder 创建数据库实例
     * 使用 @Singleton 确保全局单例
     *
     * @param context 应用上下文
     * @return AppDatabase 数据库实例
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // 允许降级时删除并重建（生产环境建议使用迁移策略）
            .build()
    }

    /**
     * 提供 UserDao 实例
     *
     * @param database AppDatabase 实例
     * @return UserDao 用户数据访问对象
     */
    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}
