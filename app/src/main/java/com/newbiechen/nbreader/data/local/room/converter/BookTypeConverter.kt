package com.newbiechen.nbreader.data.local.room.converter

import androidx.room.TypeConverter
import com.newbiechen.nbreader.ui.component.book.type.BookType

/**
 *  author : newbiechen
 *  date : 2019-09-21 15:38
 *  description :
 */

class BookTypeConverter {

    @TypeConverter
    fun strToType(type: String): BookType {
        return BookType.valueOf(type)
    }

    @TypeConverter
    fun typeToStr(bookType: BookType): String {
        return bookType.name
    }
}