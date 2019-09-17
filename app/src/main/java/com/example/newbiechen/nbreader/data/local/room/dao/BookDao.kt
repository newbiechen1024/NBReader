package com.example.newbiechen.nbreader.data.local.room.dao

import androidx.room.*
import com.example.newbiechen.nbreader.ui.component.book.entity.Book

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:22
 *  description :阅读器书籍数据
 */

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Update
    fun updateBook(book: Book)

    @Delete
    fun removeBook(book: Book)

/*    fun removeBookById(book: Book)

    fun getBookById(id: Long)
    fun getBookByPath(path: String)*/
}
