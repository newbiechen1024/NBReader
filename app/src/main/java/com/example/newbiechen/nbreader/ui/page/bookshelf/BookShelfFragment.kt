package com.example.newbiechen.nbreader.ui.page.bookshelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.youtubedl.ui.main.base.BaseBindingFragment

class BookShelfFragment : BaseBindingFragment<FragmentBookShelfBinding>() {
    override fun initContentView(): Int = R.layout.fragment_book_shelf

    companion object {
        fun newInstance() = BookShelfFragment()
    }
}