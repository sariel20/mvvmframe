package com.lc.mvvmframe

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

/**
 * MVVM 框架 Application 基类
 *
 * 继承此类并添加 @HiltAndroidApp 注解来启用 Hilt 依赖注入
 * 实现 ImageLoaderFactory 接口自定义 Coil 图片加载配置
 *
 * 注意：库模块不需 @HiltAndroidApp，请在 app 模块的 Application 类上添加
 *
 * @author 小龙虾
 * @created 2024/01/01
 */
open class MvvmFrameApp : Application(), ImageLoaderFactory {

    companion object {
        @Volatile
        private var instance: MvvmFrameApp? = null

        fun getInstance(): MvvmFrameApp? = instance
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        // 可以在此初始化全局配置，如日志、崩溃监控等
    }

    /**
     * 自定义 Coil 图片加载器配置
     *
     * - 内存缓存：使用 App 可用内存的 25%
     * - 磁盘缓存：50MB
     * - 缓存策略：优先使用缓存
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25) // 使用 25% 可用内存
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB 磁盘缓存
                    .build()
            }
            .memoryCachePolicy(CachePolicy.ENABLED) // 启用内存缓存
            .diskCachePolicy(CachePolicy.ENABLED)   // 启用磁盘缓存
            .crossfade(true) // 启用淡入动画
            .build()
    }
}
