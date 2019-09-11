package com.example.newbiechen.nbreader.ui.page.bookshelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.example.newbiechen.nbreader.ui.page.read.ReadActivity
import com.youtubedl.ui.main.base.BaseBindingFragment

class BookShelfFragment : BaseBindingFragment<FragmentBookShelfBinding>() {


    override fun initContentView(): Int = R.layout.fragment_book_shelf

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun test(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

        fun newInstance() = BookShelfFragment()
    }

    override fun initView() {
        super.initView()

        mDataBinding.tvRead.text = stringFromJNI()

        mDataBinding.tvRead.setOnClickListener {
            startActivity(ReadActivity::class)
        }
    }
}