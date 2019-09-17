package com.example.newbiechen.nbreader.ui.component.book.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  author : newbiechen
 *  date : 2019-09-16 16:45
 *  description :阅读器书籍的数据结构
 */

@Entity(tableName = "nb_book")
class Book(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = -1,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "encoding")
    var encoding: String? = null,
    @ColumnInfo(name = "lang")
    var lang: String? = null
)