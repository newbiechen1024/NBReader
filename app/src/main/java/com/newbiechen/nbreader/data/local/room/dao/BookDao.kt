package com.newbiechen.nbreader.data.local.room.dao

import androidx.room.*
import com.newbiechen.nbreader.data.entity.BookEntity
import io.reactivex.Maybe

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:22
 *  description :阅读器书籍数据
 */

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<BookEntity>)

    @Update
    fun updateBook(book: BookEntity)

    @Delete
    fun removeBook(book: BookEntity)

    @Query("SELECT * FROM book_entity")
    fun getAllBooks(): Maybe<List<BookEntity>>
}
