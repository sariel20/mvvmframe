package com.lc.mvvmframe.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 *
 * @author 小龙虾
 */
object TimeUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    /**
     * 格式化日期
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的日期字符串
     */
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    /**
     * 格式化日期时间
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的日期时间字符串
     */
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    /**
     * 格式化时间
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的时间字符串
     */
    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    /**
     * 获取相对时间
     *
     * 如：刚刚、5分钟前、1小时前、昨天、前天、具体日期
     *
     * @param timestamp 时间戳（毫秒）
     * @return 相对时间字符串
     */
    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 2 * 24 * 60 * 60 * 1000 -> "昨天"
            diff < 3 * 24 * 60 * 60 * 1000 -> "前天"
            else -> formatDate(timestamp)
        }
    }

    /**
     * 判断是否为同一天
     *
     * @param timestamp1 时间戳1
     * @param timestamp2 时间戳2
     * @return 是否为同一天
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 判断是否为今天
     *
     * @param timestamp 时间戳
     * @return 是否为今天
     */
    fun isToday(timestamp: Long): Boolean {
        return isSameDay(timestamp, System.currentTimeMillis())
    }

    /**
     * 获取今天的开始时间戳
     *
     * @return 今天 00:00:00 的时间戳
     */
    fun getTodayStartTimestamp(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
