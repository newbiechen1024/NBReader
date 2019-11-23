package com.example.newbiechen.nbreader.ui.page.bookshelf

import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.ui.page.read.ReadActivity
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import java.io.File
import java.io.FileInputStream

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

            // 指定一个文件路径
            ReadActivity.startActivity(context!!,
                // /storage/emulated/0/测试书籍/修罗帝尊[www.txt909.com].txt
                BookEntity("asda", "修罗帝尊", BookType.TXT, "/storage/emulated/0/测试书籍/修罗帝尊[www.txt909.com].txt", true)
            )
        }
    }
}