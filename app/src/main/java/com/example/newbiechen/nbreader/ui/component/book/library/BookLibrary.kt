package com.example.newbiechen.nbreader.ui.component.book.library

import android.os.Binder
import com.example.newbiechen.nbreader.ui.component.book.entity.Book

/**
 *  author : newbiechen
 *  date : 2019-09-16 17:09
 *  description :书籍存取
 */

class BookLibrary:IBookLibrary {

    override fun getBookById(id: Long): Book {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookByPath(path: String): Book {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRecentBook(): Book {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveBook(book: Book) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeBookById(id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}