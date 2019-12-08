package com.example.newbiechen.nbreader.ui.page.bookshelf

import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.ui.page.read.ReadActivity
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.regex.Matcher
import java.util.regex.Pattern

class BookShelfFragment : BaseBindingFragment<FragmentBookShelfBinding>() {


    override fun initContentView(): Int = R.layout.fragment_book_shelf

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun test(): String

    companion object {
        fun newInstance() = BookShelfFragment()
    }


    override fun initView() {
        super.initView()
        mDataBinding.tvRead.text = stringFromJNI()
        mDataBinding.tvRead.setOnClickListener {

            // 指定一个文件路径 /sdcard/测试书籍/剑来.txt
            // 重生后嫁给克妻皇帝
            // zh-gb18030
            ReadActivity.startActivity(context!!,
                BookEntity("asda", "剑来", BookType.TXT, "/storage/emulated/0/测试书籍/剑来.txt", true)
            )
        }
    }
}