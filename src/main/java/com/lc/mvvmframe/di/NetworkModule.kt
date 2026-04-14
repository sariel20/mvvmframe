package com.lc.mvvmframe.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.lc.mvvmframe.data.local.sp.PreferencesManager
import com.lc.mvvmframe.data.remote.api.UserApi
import com.lc.mvvmframe.network.SessionAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 网络模块
 *
 * 提供网络相关的依赖注入配置：
 * - OkHttpClient
 * - Retrofit
 * - Gson
 * - API 接口
 *
 * @author Cheng
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 基础 URL - 可以通过 BuildConfig 或远程配置获取
    private const val BASE_URL = "https://api.example.com/"

    /**
     * 提供 Gson 实例
     *
     * 配置日期格式等
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 日期格式
            .serializeNulls() // 序列化 null 值
            .create()
    }

    /**
     * 提供 Token 拦截器
     *
     * 自动为请求添加 Authorization Header
     */
    @Provides
    @Singleton
    fun provideTokenInterceptor(preferencesManager: PreferencesManager): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val token = preferencesManager.authToken

            // 如果有 Token，添加到请求头
            val newRequest = if (!token.isNullOrEmpty()) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }

            chain.proceed(newRequest)
        }
    }

    /**
     * 提供 OkHttpClient 实例
     *
     * 配置：
     * - 连接超时
     * - 读取超时
     * - 写入超时
     * - 日志拦截器（仅 debug 模式）
     * - Token 拦截器
     *
     * @author Cheng
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: Interceptor,
        sessionAuthenticator: SessionAuthenticator,
    ): OkHttpClient {
        // 日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // 作为框架库：默认使用 BASIC，避免依赖 BuildConfig.DEBUG（不同模块命名空间下可能不可用）
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)   // 连接超时 30s
            .readTimeout(30, TimeUnit.SECONDS)      // 读取超时 30s
            .writeTimeout(30, TimeUnit.SECONDS)     // 写入超时 30s
            .addInterceptor(loggingInterceptor)     // 日志拦截器
            .addInterceptor(tokenInterceptor)       // Token 拦截器
            .authenticator(sessionAuthenticator)    // 401 统一处理
            .build()
    }

    /**
     * 提供 Retrofit 实例
     *
     * 配置：
     * - 基础 URL
     * - Gson 转换器
     * - OkHttpClient
     *
     * @author Cheng
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * 提供 UserApi 实例
     */
    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}
