package com.lc.mvvmframe.data.remote.api

import com.lc.mvvmframe.domain.model.BaseResponse
import com.lc.mvvmframe.domain.model.User
import retrofit2.http.*

/**
 * 用户相关 API 接口
 *
 * 定义所有用户相关的网络请求接口
 * 使用 Retrofit 注解声明 HTTP 请求
 *
 * @author 小龙虾
 */
interface UserApi {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码（建议在前端加密后传输）
     * @return BaseResponse<User> 登录结果
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<User>

    /**
     * 获取用户信息
     *
     * 需要携带认证 Token
     *
     * @return BaseResponse<User> 用户信息
     */
    @GET("user/info")
    suspend fun getUserInfo(): BaseResponse<User>

    /**
     * 更新用户信息
     *
     * @param user 更新后的用户信息
     * @return BaseResponse<User> 更新结果
     */
    @POST("user/update")
    suspend fun updateUser(@Body user: User): BaseResponse<User>

    /**
     * 发送验证码
     *
     * @param phone 手机号
     * @param type 验证码类型（注册/登录/找回密码等）
     * @return BaseResponse<String> 发送结果
     */
    @GET("sms/send")
    suspend fun sendSms(
        @Query("phone") phone: String,
        @Query("type") type: String
    ): BaseResponse<String>

    /**
     * 用户注册
     *
     * @param phone 手机号
     * @param password 密码
     * @param code 验证码
     * @return BaseResponse<User> 注册结果
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("code") code: String
    ): BaseResponse<User>
}
