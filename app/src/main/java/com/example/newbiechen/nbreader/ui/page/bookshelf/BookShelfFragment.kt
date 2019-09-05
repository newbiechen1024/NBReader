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

    companion object {
        fun newInstance() = BookShelfFragment()
    }

    override fun initView() {
        super.initView()

        mDataBinding.tvRead.setOnClickListener {
            startActivity(ReadActivity::class)
        }
    }
}