package com.newbiechen.nbreader.ui.page.mine

import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.databinding.FragmentMineBinding
import com.newbiechen.nbreader.ui.page.base.BaseBindingFragment

class MineFragment : BaseBindingFragment<FragmentMineBinding>() {
    companion object {
        fun newInstance() = MineFragment()
    }

    override fun initContentView(): Int = R.layout.fragment_mine
}
