package com.lc.mvvmframe.data.local.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Room 数据库类型转换器
 *
 * 用于处理 Room 无法直接存储的复杂类型：
 * - List<String>
 * - Map<String, Any>
 * - 自定义对象
 *
 * 使用 Gson 序列化/反序列化
 *
 * @author 小龙虾
 */
class Converters {

    private val gson = Gson()

    /**
     * 存储 List<String> 为 JSON 字符串
     */
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * 从 JSON 字符串读取 List<String>
     */
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    /**
     * 存储 Map<String, String> 为 JSON 字符串
     */
    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    /**
     * 从 JSON 字符串读取 Map<String, String>
     */
    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
