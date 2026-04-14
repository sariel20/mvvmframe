package com.lc.mvvmframe.domain.repository

import com.lc.mvvmframe.domain.model.Resource
import com.lc.mvvmframe.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户仓库接口
 *
 * 定义用户相关的数据操作契约，包括：
 * - 网络请求（登录、获取用户信息等）
 * - 本地缓存（用户信息的持久化）
 *
 * 采用接口分离原则，便于单元测试时使用 Mock
 *
 * @author Cheng
 */
interface UserRepository {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return Resource<User> 登录结果
     */
    suspend fun login(username: String, password: String): Resource<User>

    /**
     * 获取当前登录用户信息
     *
     * 优先从本地缓存获取，如果缓存不存在或过期则从网络请求
     *
     * @return Resource<User> 用户信息
     */
    suspend fun getUserInfo(): Resource<User>

    /**
     * 刷新用户信息
     *
     * 强制从网络获取最新数据并更新本地缓存
     *
     * @return Resource<User> 用户信息
     */
    suspend fun refreshUserInfo(): Resource<User>

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return Resource<User> 更新后的用户信息
     */
    suspend fun updateUser(user: User): Resource<User>

    /**
     * 退出登录
     *
     * 清除本地缓存的用户信息
     */
    suspend fun logout()

    /**
     * 观察当前用户信息
     *
     * 返回 Flow 用于响应式更新，返回缓存数据并自动监听变化
     *
     * @return Flow<User?> 用户信息流
     */
    fun observeUser(): Flow<User?>

    /**
     * 判断用户是否已登录
     *
     * @return Boolean 是否已登录
     */
    fun isLoggedIn(): Boolean
}
