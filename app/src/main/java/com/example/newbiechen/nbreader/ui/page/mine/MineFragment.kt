package com.example.newbiechen.nbreader.ui.page.mine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newbiechen.nbreader.databinding.FragmentMineBinding
import com.youtubedl.ui.main.base.BaseFragment
import javax.inject.Inject

class MineFragment : BaseFragment() {

    private lateinit var mDataBinding: FragmentMineBinding

    companion object {
        fun newInstance() = MineFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDataBinding = FragmentMineBinding.inflate(inflater, container, false)
        return mDataBinding.root
    }
}
