package com.example.newbiechen.nbreader.data.entity.book

import android.os.Parcel
import android.os.Parcelable
import com.example.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-18 15:12
 *  description :书本信息
 */


data class BookEntity(
    // 书籍元数据信息
    val id: Long, // 书本 id
    val name: String, // 书本名
    val author: String, // 作者名
    val type: BookType, // 书本类型
    val url: String, // 书籍来源
    var cover: String? = null, // 书籍封面
    var encoding: String, // 书籍编码
    var lang: String,// 书籍语言
    val isLocal: Boolean,// 是否是本地书籍
    val curChapter: String? = null, // 当前章节名
    var totalChapter: Int? = null, // 章节总数
    val lastChapter: String? = null, // 最新章节名
    val isUpdate: Boolean = true // 是否更新

    // 暂时不需要，以后加
    // val updateTime: String, // 书本更新时间
    // val recentTime: String // 最近阅读时间
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        BookType.valueOf(parcel.readString()),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(author)
        parcel.writeString(type.name)
        parcel.writeString(url)
        parcel.writeString(cover)
        parcel.writeString(encoding)
        parcel.writeString(lang)
        parcel.writeByte(if (isLocal) 1 else 0)
        parcel.writeString(curChapter)
        parcel.writeValue(totalChapter)
        parcel.writeString(lastChapter)
        parcel.writeByte(if (isUpdate) 1 else 0)
    }

    override fun describeContents(): Int {
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