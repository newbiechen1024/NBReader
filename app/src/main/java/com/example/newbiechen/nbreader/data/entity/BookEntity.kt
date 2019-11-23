package com.example.newbiechen.nbreader.data.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.newbiechen.nbreader.data.local.room.converter.BookTypeConverter
import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-18 15:12
 *  description :书本信息
 */

@Entity(tableName = "book_entity")
@TypeConverters(BookTypeConverter::class)
data class BookEntity(
    // 书籍元数据信息
    @PrimaryKey
    val id: String, // 书本 id
    @ColumnInfo(name = "title")
    val title: String, // 书本名
    @ColumnInfo(name = "type")
    val type: BookType, // 书本类型
    @ColumnInfo(name = "url")
    val url: String, // 书籍来源
    @ColumnInfo(name = "isLocal")
    val isLocal: Boolean,// 是否是本地书籍
    @ColumnInfo(name = "author")
    var author: String? = null, // 作者名
    @ColumnInfo(name = "cover")
    var cover: String? = null, // 书籍封面
    @ColumnInfo(name = "encoding")
    var encoding: String? = null, // 书籍编码
    @ColumnInfo(name = "lang")
    var lang: String? = null,// 书籍语言
    @ColumnInfo(name = "curChapter")
    var curChapter: String? = null, // 当前章节名
    @ColumnInfo(name = "totalChapter")
    var totalChapter: Int? = null, // 章节总数
    @ColumnInfo(name = "lastChapter")
    var lastChapter: String? = null, // 最新章节名
    @ColumnInfo(name = "isUpdate")
    var isUpdate: Boolean = true // 是否更新

    // 暂时不需要，以后加
    // val updateTime: String, // 书本更新时间
    // val recentTime: String // 最近阅读时间
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        BookType.valueOf(parcel.readString()),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(type.name)
        parcel.writeString(url)
        parcel.writeByte(if (isLocal) 1 else 0)
        parcel.writeString(author)
        parcel.writeString(cover)
        parcel.writeString(encoding)
        parcel.writeString(lang)
        parcel.writeString(curChapter)
        parcel.writeValue(totalChapter)
        parcel.writeString(lastChapter)
        parcel.writeByte(if (isUpdate) 1 else 0)
    }

    override fun describeContents(): Int {
        var charSet = Charsets.UTF_8
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookEntity> {
        override fun createFromParcel(parcel: Parcel): BookEntity {
            return BookEntity(parcel)
        }

        override fun newArray(size: Int): Array<BookEntity?> {
            return arrayOfNulls(size)
        }
    }
}