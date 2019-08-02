package com.example.newbiechen.nbreader.data.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookCoverConverter {

    @TypeConverter
    fun jsonToObject(json: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(json, listType)
    }

    @TypeConverter
    fun objectToJson(list: List<String>): String {
        return Gson().toJson(list)
    }
}