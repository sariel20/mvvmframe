package com.lc.mvvmframe.data.repository

import com.lc.mvvmframe.data.local.db.UserDao
import com.lc.mvvmframe.data.local.sp.PreferencesManager
import com.lc.mvvmframe.data.remote.api.UserApi
import com.lc.mvvmframe.core.coroutines.DispatcherProvider
import com.lc.mvvmframe.domain.model.Resource
import com.lc.mvvmframe.domain.model.User
import com.lc.mvvmframe.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户仓库实现类
 *
 * 实现 UserRepository 接口，负责：
 * 1. 网络请求 - 调用 API 获取数据
 * 2. 本地缓存 - 读写 Room 数据库和 SharedPreferences
 * 3. 数据统一 - 对外提供统一的数据访问接口
 *
 * 使用 @Singleton 注解确保全局单例
 * 使用 @Inject 注解让 Hilt 自动注入依赖
 *
 * @author Cheng
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApi: UserApi,           // 网络 API
    private val userDao: UserDao,           // 本地数据库 DAO
    private val preferencesManager: PreferencesManager, // SharedPreferences
    private val dispatchers: DispatcherProvider,
) : BaseRepository(), UserRepository {

    /**
     * 用户登录
     *
     * 流程：
     * 1. 调用登录 API
     * 2. 保存 Token 和用户信息到本地
     * 3. 返回结果
     */
    override suspend fun login(username: String, password: String): Resource<User> {
        return withContext(dispatchers.io) {
            val result = safeApiCall { userApi.login(username, password) }
            if (result is Resource.Success) {
                // 登录成功，保存用户信息和 Token（token 字段示例：当前 API 复用了 msg）
                saveUserAndToken(result.data, token = null)
            }
            result
        }
    }

    /**
     * 获取用户信息
     *
     * 流程：
     * 1. 检查本地缓存是否有效
     * 2. 如果缓存有效，返回缓存数据
     * 3. 如果缓存无效或不存在，请求网络
     * 4. 更新本地缓存并返回
     */
    override suspend fun getUserInfo(): Resource<User> {
        return withContext(dispatchers.io) {
            // 优先检查本地缓存
            val cachedUser = getCachedUser()
            if (cachedUser != null && preferencesManager.isUserCacheValid) {
                return@withContext Resource.Success(cachedUser)
            }

            // 缓存无效，请求网络；失败则回落缓存
            when (val net = safeApiCall { userApi.getUserInfo() }) {
                is Resource.Success -> {
                    saveUserToDb(net.data)
                    preferencesManager.userCacheTime = System.currentTimeMillis()
                    net
                }
                is Resource.Error -> cachedUser?.let { Resource.Success(it) } ?: net
                is Resource.Loading -> net
            }
        }
    }

    /**
     * 刷新用户信息
     *
     * 强制从网络获取最新数据
     */
    override suspend fun refreshUserInfo(): Resource<User> {
        return withContext(dispatchers.io) {
            when (val net = safeApiCall { userApi.getUserInfo() }) {
                is Resource.Success -> {
                    saveUserToDb(net.data)
                    preferencesManager.userCacheTime = System.currentTimeMillis()
                    net
                }
                else -> net
            }
        }
    }

    /**
     * 更新用户信息
     */
    override suspend fun updateUser(user: User): Resource<User> {
        return withContext(dispatchers.io) {
            when (val net = safeApiCall { userApi.updateUser(user) }) {
                is Resource.Success -> {
                    saveUserToDb(net.data)
                    net
                }
                else -> net
            }
        }
    }

    /**
     * 退出登录
     */
    override suspend fun logout() {
        withContext(dispatchers.io) {
            // 清除本地数据库中的用户信息
            userDao.deleteAll()
            // 清除 SharedPreferences 中的登录数据
            preferencesManager.clearLoginData()
        }
    }

    /**
     * 观察当前用户信息
     *
     * 返回 Flow，当本地数据库变化时自动推送新数据
     */
    override fun observeUser(): Flow<User?> {
        val userId = preferencesManager.currentUserId
        return userDao.observeUser(userId)
    }

    /**
     * 判断用户是否已登录
     */
    override fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 保存用户信息和 Token
     */
    private suspend fun saveUserAndToken(user: User, token: String?) {
        // 保存 Token（如果有返回）
        token?.let { preferencesManager.authToken = it }
        // 保存用户 ID
        preferencesManager.currentUserId = user.id
        // 保存到数据库
        saveUserToDb(user)
        // 更新缓存时间
        preferencesManager.userCacheTime = System.currentTimeMillis()
    }

    /**
     * 保存用户到数据库
     */
    private suspend fun saveUserToDb(user: User) {
        userDao.insertOrUpdate(user)
    }

    /**
     * 获取缓存的用户
     */
    private suspend fun getCachedUser(): User? {
        val userId = preferencesManager.currentUserId
        return if (userId > 0) {
            userDao.getUserById(userId)
        } else {
            null
        }
    }
}
