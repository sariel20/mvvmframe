package com.lc.mvvmframe.data.repository

import com.lc.mvvmframe.domain.model.BaseResponse
import com.lc.mvvmframe.domain.model.Resource
import com.lc.mvvmframe.network.NetworkErrorMapper

/**
 * Repository 基类：统一 try/catch、业务错误判断与异常映射。
 */
abstract class BaseRepository {

    protected suspend fun <T> safeApiCall(block: suspend () -> BaseResponse<T>): Resource<T> {
        return try {
            val response = block()
            responseToResource(response)
        } catch (t: Throwable) {
            val mapped = NetworkErrorMapper.fromThrowable(t)
            Resource.Error(mapped.message, t)
        }
    }

    protected fun <T> responseToResource(response: BaseResponse<T>): Resource<T> {
        val data = response.data
        return if (response.isSuccess && data != null) {
            Resource.Success(data)
        } else {
            val appError = NetworkErrorMapper.business(response.code, response.errorMsg)
            Resource.Error(appError.message)
        }
    }
}

