package com.example.newbiechen.nbreader.ui.page.mine

import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.FragmentMineBinding
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment

class MineFragment : BaseBindingFragment<FragmentMineBinding>() {
    companion object {
        fun newInstance() = MineFragment()
    }

    override fun initContentView(): Int = R.layout.fragment_mine
}
