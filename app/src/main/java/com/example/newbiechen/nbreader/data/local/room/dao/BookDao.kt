package com.example.newbiechen.nbreader.data.local.room.dao

import androidx.room.*
import com.example.newbiechen.nbreader.data.entity.book.BookEntity

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:22
 *  description :阅读器书籍数据
 */

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: BookEntity)

    @Update
    fun updateBook(book: BookEntity)

    @Delete
    fun removeBook(book: BookEntity)

/*    fun removeBookById(book: Book)

    fun getBookById(id: Long)
    fun getBookByPath(path: String)*/
}
