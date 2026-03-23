package com.lc.mvvmframe.data.local.db

import androidx.room.*
import com.lc.mvvmframe.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * 用户数据访问对象 (DAO)
 *
 * 定义用户数据的本地 CRUD 操作
 * 使用 Room 注解声明 SQL 语句
 *
 * @author 小龙虾
 */
@Dao
interface UserDao {

    /**
     * 插入或更新用户信息
     *
     * 使用 @Insert(onConflict = OnConflictStrategy.REPLACE) 实现：
     * - 如果用户不存在，则插入
     * - 如果用户已存在，则更新
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return User? 用户信息，不存在则返回 null
     */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    /**
     * 观察用户信息变化
     *
     * 返回 Flow，当数据库变化时会自动推送新数据
     * 适用于 Compose 或 LiveData 观察 UI 更新
     *
     * @param id 用户 ID
     * @return Flow<User?> 用户信息流
     */
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUser(id: Long): Flow<User?>

    /**
     * 查询所有用户
     *
     * @return List<User> 用户列表
     */
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    /**
     * 删除用户
     *
     * @param user 要删除的用户
     */
    @Delete
    suspend fun delete(user: User)

    /**
     * 删除所有用户
     *
     * 通常在退出登录时调用，清空本地缓存
     */
    @Query("DELETE FROM users")
    suspend fun deleteAll()

    /**
     * 获取当前登录用户
     *
     * 假设当前登录用户 ID 存储在 SharedPreferences 中
     *
     * @param currentUserId 当前用户 ID
     * @return User? 当前用户
     */
    @Query("SELECT * FROM users WHERE id = :currentUserId")
    suspend fun getCurrentUser(currentUserId: Long): User?

    /**
     * 观察当前登录用户
     *
     * @param currentUserId 当前用户 ID
     * @return Flow<User?> 当前用户流
     */
    @Query("SELECT * FROM users WHERE id = :currentUserId")
    fun observeCurrentUser(currentUserId: Long): Flow<User?>
}
