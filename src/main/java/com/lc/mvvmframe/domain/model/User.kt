package com.lc.mvvmframe.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * 用户实体类
 *
 * 使用 @Entity 注解标记为 Room 数据库实体表
 * 同时作为领域模型和数据库实体使用
 *
 * @author Cheng
 */
@Entity(tableName = "users")
data class User(
    /**
     * 用户唯一标识
     */
    @PrimaryKey
    @SerializedName("id")
    val id: Long = 0,

    /**
     * 用户名
     */
    @SerializedName("username")
    val username: String? = null,

    /**
     * 昵称
     */
    @SerializedName("nickname")
    val nickname: String? = null,

    /**
     * 头像 URL
     */
    @SerializedName("avatar")
    val avatar: String? = null,

    /**
     * 手机号
     */
    @SerializedName("phone")
    val phone: String? = null,

    /**
     * 邮箱
     */
    @SerializedName("email")
    val email: String? = null,

    /**
     * 性别：0-未知，1-男，2-女
     */
    @SerializedName("gender")
    val gender: Int = 0,

    /**
     * 创建时间
     */
    @SerializedName("createTime")
    val createTime: Long = 0,

    /**
     * 更新时间
     */
    @SerializedName("updateTime")
    val updateTime: Long = 0
) {
    /**
     * 获取显示名称，优先使用昵称
     */
    val displayName: String
        get() = nickname ?: username ?: "未设置"
}
