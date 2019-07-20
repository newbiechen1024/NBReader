package com.example.newbiechen.nbreader.ui.page.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ActivityMainBinding
import com.example.newbiechen.nbreader.ui.component.adapter.MainPagerAdapter
import com.example.newbiechen.nbreader.ui.component.widget.TabView
import com.example.newbiechen.nbreader.uilts.factory.FragmentFactory
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.youtubedl.ui.main.base.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {

    // Fragment 工厂
    @Inject
    lateinit var mFragmentFactory: FragmentFactory
    // ViewModel 工厂
    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    private lateinit var mViewModel: MainViewModel

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainViewModel::class.java)
        mDataBinding.viewModel = mViewModel
        initView()
    }

    private fun initView() {
        // 初始化 ViewPager
        mDataBinding.viewPager.adapter = MainPagerAdapter(supportFragmentManager, mFragmentFactory)
        mDataBinding.viewPager.offscreenPageLimit = 3
        mDataBinding.bottomBar.apply {
            // 设置选中监听
            addOnTabSelectedListener(mTabSelectedListener)
            // 设置 Tab 按钮
            for (i in 0 until mFragmentFactory.getCount()) {
                addTab(
                    newTab().setCustomView(
                        mFragmentFactory.createFragmentTabView(
                            context, i
                        )
                    )
                )
            }
        }
    }

    private var mTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
            var tabView: TabView? = p0?.customView as TabView
            tabView?.setChecked(false)
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            // 设置当前显示的索引
            mViewModel.mCurPagePos.set(mDataBinding.bottomBar.selectedTabPosition)
        }
    }
}
