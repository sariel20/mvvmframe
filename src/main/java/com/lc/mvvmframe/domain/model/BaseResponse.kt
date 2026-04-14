package com.lc.mvvmframe.domain.model

import com.google.gson.annotations.SerializedName

/**
 * 网络请求响应基类
 *
 * 绝大多数 RESTful API 都遵循以下格式：
 * {
 *     "code": 200,
 *     "msg": "success",
 *     "data": { ... }
 * }
 *
 * @param T 数据类型
 * @author Cheng
 */
data class BaseResponse<T>(
    /**
     * 响应码，200 通常表示成功
     */
    @SerializedName("code")
    val code: Int = -1,

    /**
     * 响应消息，成功时通常为 "success" 或 ""
     */
    @SerializedName("msg")
    val msg: String? = null,

    /**
     * 响应数据
     */
    @SerializedName("data")
    val data: T? = null
) {
    /**
     * 判断是否为成功的响应
     * 可根据实际情况调整成功码判断逻辑
     */
    val isSuccess: Boolean
        get() = code == 200 || code == 0 || code == 1

    /**
     * 获取错误消息
     */
    val errorMsg: String
        get() = msg ?: "未知错误"
}
