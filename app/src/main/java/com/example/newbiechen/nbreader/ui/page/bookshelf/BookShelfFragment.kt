package com.example.newbiechen.nbreader.ui.page.bookshelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.youtubedl.ui.main.base.BaseFragment
import javax.inject.Inject

class BookShelfFragment : BaseFragment() {

    private lateinit var mDataBinding: FragmentBookShelfBinding

    companion object {
        fun newInstance() = BookShelfFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDataBinding = FragmentBookShelfBinding.inflate(inflater, container, false)
        return mDataBinding.root
    }
}