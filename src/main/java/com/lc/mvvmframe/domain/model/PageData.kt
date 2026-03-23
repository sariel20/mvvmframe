package com.lc.mvvmframe.domain.model

import com.google.gson.annotations.SerializedName

/**
 * 分页数据封装类
 *
 * 用于处理分页列表数据，如：
 * {
 *     "list": [...],
 *     "total": 100,
 *     "page": 1,
 *     "pageSize": 20
 * }
 *
 * @param T 列表数据类型
 * @author 小龙虾
 */
data class PageData<T>(
    /**
     * 数据列表
     */
    @SerializedName("list")
    val list: List<T>? = null,

    /**
     * 总数据条数
     */
    @SerializedName("total")
    val total: Int = 0,

    /**
     * 当前页码
     */
    @SerializedName("page")
    val page: Int = 1,

    /**
     * 每页大小
     */
    @SerializedName("pageSize")
    val pageSize: Int = 20
) {
    /**
     * 判断是否还有更多数据
     */
    val hasMore: Boolean
        get() = (list?.size ?: 0) >= pageSize

    /**
     * 获取下一页页码
     */
    val nextPage: Int
        get() = page + 1

    /**
     * 是否是第一页
     */
    val isFirstPage: Boolean
        get() = page == 1
}
