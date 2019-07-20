package com.example.newbiechen.nbreader.ui.page.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newbiechen.nbreader.databinding.FragmentFindBinding
import com.youtubedl.ui.main.base.BaseFragment

class FindFragment : BaseFragment() {

    private lateinit var mDataBinding: FragmentFindBinding

    companion object {
        fun newInstance() = FindFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDataBinding = FragmentFindBinding.inflate(inflater, container, false)
        return mDataBinding.root
    }
}