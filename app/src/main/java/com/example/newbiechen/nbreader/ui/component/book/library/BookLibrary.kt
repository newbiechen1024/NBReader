package com.example.newbiechen.nbreader.ui.component.book.library

import android.os.Binder
import com.example.newbiechen.nbreader.data.entity.book.BookEntity

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:09
 *  description :书籍存取
 */

class BookLibrary : IBookLibrary {

    override fun getBookById(id: String): BookEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookByPath(path: String): BookEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecentBook(): BookEntity {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveBook(book: BookEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookById(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}