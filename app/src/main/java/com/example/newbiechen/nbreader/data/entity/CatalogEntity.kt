package com.example.newbiechen.nbreader.data.entity

import androidx.annotation.NonNull
import androidx.room.*
import com.example.newbiechen.nbreader.data.local.room.converter.BookCoverConverter
import com.google.gson.annotations.SerializedName

/**
 * Wrapper:表示用于从 api 获取的数据
 * Entity:表示用于存储到数据库的数据
 */
data class CatalogWrapper(
    // 女生分类
    @SerializedName("female")
    val female: List<CatalogEntity>,
    // 男生分类
    @SerializedName("male")
    val male: List<CatalogEntity>,
    val ok: Boolean
)

@Entity(tableName = "catalog_entity")
@TypeConverters(BookCoverConverter::class)
data class CatalogEntity(
    @NonNull
    @SerializedName("alias")
    @ColumnInfo(name = "alias")
    @PrimaryKey
    val alias: String,
    @SerializedName("name")
    @ColumnInfo(name = "name")
    val name: String,
    @SerializedName("bookCover")
    @ColumnInfo(name = "bookCover")
    val bookCover: List<String>
)