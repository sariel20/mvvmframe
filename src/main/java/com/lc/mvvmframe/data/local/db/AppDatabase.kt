package com.lc.mvvmframe.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * 应用数据库
 *
 * 声明应用中所有的 DAO 和实体类
 * 使用 @Database 注解标记
 *
 * @author Cheng
 */
@Database(
    entities = [
        com.lc.mvvmframe.domain.model.User::class
        // 在此添加其他实体类，如：
        // Article::class,
        // Product::class
    ],
    version = 1, // 数据库版本，用于迁移管理
    exportSchema = true // 导出 schema 文件，便于查看和迁移
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * 获取用户 DAO
     */
    abstract fun userDao(): UserDao

    companion object {
        // 数据库名称
        const val DATABASE_NAME = "mvvmframe_db"
    }
}
