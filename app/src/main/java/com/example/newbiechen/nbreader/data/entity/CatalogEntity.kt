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
    val ok: Boolean,
    @SerializedName("picture") // 这个分类感觉像漫画，先统一拉下载，再删吧
    val picture: List<CatalogEntity>
/*    // 其他分类 ==> 这个分类就不展示了
    @SerializedName("press")
    val other: List<CatalogEntity>*/
)

@Entity(tableName = "catalog_entity")
@TypeConverters(BookCoverConverter::class)
data class CatalogEntity(
    @NonNull
    @SerializedName("name")
    @ColumnInfo(name = "name")
    @PrimaryKey
    val name: String,
    @SerializedName("bookCover")
    @ColumnInfo(name = "bookCover")
    val bookCover:List<String>
)