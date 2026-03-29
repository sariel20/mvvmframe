package com.lc.mvvmframe.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 时间工具类 (基于 java.time API)
 *
 * @author 小龙虾
 */
object TimeUtils {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * 格式化日期
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的日期字符串
     */
    fun formatDate(timestamp: Long): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            .format(dateFormatter)
    }

    /**
     * 格式化日期时间
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的日期时间字符串
     */
    fun formatDateTime(timestamp: Long): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            .format(dateTimeFormatter)
    }

    /**
     * 格式化时间
     *
     * @param timestamp 时间戳（毫秒）
     * @return 格式化后的时间字符串
     */
    fun formatTime(timestamp: Long): String {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            .format(timeFormatter)
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
        val diffMillis = now - timestamp
        
        if (diffMillis < 60 * 1000) return "刚刚"
        if (diffMillis < 60 * 60 * 1000) return "${diffMillis / (60 * 1000)}分钟前"
        if (diffMillis < 24 * 60 * 60 * 1000) return "${diffMillis / (60 * 60 * 1000)}小时前"

        val nowLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault())
        val targetLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())

        val daysBetween = ChronoUnit.DAYS.between(targetLdt.toLocalDate(), nowLdt.toLocalDate())

        return when {
            daysBetween == 1L -> "昨天"
            daysBetween == 2L -> "前天"
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
        val date1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp1), ZoneId.systemDefault()).toLocalDate()
        val date2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp2), ZoneId.systemDefault()).toLocalDate()
        return date1 == date2
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
        return LocalDateTime.now()
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}
