package com.lc.mvvmframe.ui.base

/**
 * 基础视图接口
 *
 * 定义所有 View（Activity/Fragment）需要实现的基本功能：
 * - 显示加载中
 * - 显示错误信息
 * - 显示空状态
 *
 * @author Cheng
 */
interface IBaseView {

    /**
     * 显示加载中
     *
     * @param message 加载提示信息（可选）
     */
    fun showLoading(message: String? = null)

    /**
     * 隐藏加载中
     */
    fun hideLoading()

    /**
     * 显示错误信息
     *
     * @param message 错误信息
     * @param retry 是否显示重试按钮
     */
    fun showError(message: String, retry: Boolean = false)

    /**
     * 显示空状态
     *
     * @param message 空状态提示信息
     * @param icon 图标资源（可选）
     */
    fun showEmpty(message: String, icon: Int? = null)

    /**
     * 显示 Toast 消息
     *
     * @param message 消息内容
     */
    fun showToast(message: String)
}
