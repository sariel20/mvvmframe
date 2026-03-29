package com.lc.mvvmframe.data.local.sp

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SharedPreferences 管理类
 *
 * 用于存储轻量级的键值对数据，如：
 * - 用户登录状态
 * - 用户 Token
 * - 用户 ID
 * - 应用设置
 *
 * 使用 Hilt @Singleton 确保全局单例
 *
 * @author 小龙虾
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sp: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    // ==================== Token 相关 ====================

    /**
     * 保存用户 Token
     */
    var authToken: String?
        get() = sp.getString(KEY_AUTH_TOKEN, null)
        set(value) = sp.edit().putString(KEY_AUTH_TOKEN, value).apply()

    /**
     * 保存刷新 Token
     */
    var refreshToken: String?
        get() = sp.getString(KEY_REFRESH_TOKEN, null)
        set(value) = sp.edit().putString(KEY_REFRESH_TOKEN, value).apply()

    /**
     * 清除 Token
     */
    fun clearToken() {
        sp.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    // ==================== 用户信息相关 ====================

    /**
     * 保存当前用户 ID
     */
    var currentUserId: Long
        get() = sp.getLong(KEY_CURRENT_USER_ID, -1L)
        set(value) = sp.edit().putLong(KEY_CURRENT_USER_ID, value).apply()

    /**
     * 判断用户是否已登录
     */
    val isLoggedIn: Boolean
        get() = !authToken.isNullOrEmpty() && currentUserId > 0

    // ==================== 应用设置相关 ====================

    /**
     * 是否首次启动
     */
    var isFirstLaunch: Boolean
        get() = sp.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = sp.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    /**
     * 应用主题模式：0-跟随系统，1-明亮，2-暗黑
     */
    var themeMode: Int
        get() = sp.getInt(KEY_THEME_MODE, 0)
        set(value) = sp.edit().putInt(KEY_THEME_MODE, value).apply()

    /**
     * 记住密码
     */
    var rememberPassword: Boolean
        get() = sp.getBoolean(KEY_REMEMBER_PASSWORD, false)
        set(value) = sp.edit().putBoolean(KEY_REMEMBER_PASSWORD, value).apply()

    /**
     * 保存用户名（用于记住密码功能）
     */
    var savedUsername: String?
        get() = sp.getString(KEY_SAVED_USERNAME, null)
        set(value) = sp.edit().putString(KEY_SAVED_USERNAME, value).apply()

    /**
     * 保存密码（用于记住密码功能，建议加密存储）
     */
    var savedPassword: String?
        get() = sp.getString(KEY_SAVED_PASSWORD, null)
        set(value) = sp.edit().putString(KEY_SAVED_PASSWORD, value).apply()

    // ==================== 缓存相关 ====================

    /**
     * 用户信息缓存时间戳
     */
    var userCacheTime: Long
        get() = sp.getLong(KEY_USER_CACHE_TIME, 0L)
        set(value) = sp.edit().putLong(KEY_USER_CACHE_TIME, value).apply()

    /**
     * 缓存有效时间（毫秒），默认 5 分钟
     */
    var cacheValidDuration: Long
        get() = sp.getLong(KEY_CACHE_VALID_DURATION, 5 * 60 * 1000L)
        set(value) = sp.edit().putLong(KEY_CACHE_VALID_DURATION, value).apply()

    /**
     * 判断用户缓存是否有效
     */
    val isUserCacheValid: Boolean
        get() = System.currentTimeMillis() - userCacheTime < cacheValidDuration

    // ==================== 清除所有数据 ====================

    /**
     * 清除所有数据
     *
     * 用于退出登录或清除缓存
     */
    fun clearAll() {
        sp.edit().clear().apply()
    }

    /**
     * 清除登录相关数据（保留设置）
     */
    fun clearLoginData() {
        sp.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_CURRENT_USER_ID)
            .remove(KEY_USER_CACHE_TIME)
            .apply()
    }

    companion object {
        // SharedPreferences 文件名
        private const val PREFS_NAME = "mvvmframe_prefs"

        // Key 常量
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_REMEMBER_PASSWORD = "remember_password"
        private const val KEY_SAVED_USERNAME = "saved_username"
        private const val KEY_SAVED_PASSWORD = "saved_password"
        private const val KEY_USER_CACHE_TIME = "user_cache_time"
        private const val KEY_CACHE_VALID_DURATION = "cache_valid_duration"
    }
}
