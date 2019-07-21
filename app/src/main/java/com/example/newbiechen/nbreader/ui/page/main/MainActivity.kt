package com.example.newbiechen.nbreader.ui.page.main

import android.os.Bundle
import android.view.Menu
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ActivityMainBinding
import com.example.newbiechen.nbreader.ui.component.adapter.MainPagerAdapter
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.factory.FragmentFactory
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.youtubedl.ui.main.base.BaseActivity
import javax.inject.Inject

class MainActivity : BaseActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

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
        initToolbar()
        initView()
    }

    private fun initToolbar() {
        setSupportActionBar(mDataBinding.toolbar)
        supportActionBar?.title = ""
        mViewModel.curPageTitle.set(getString(R.string.common_book_shelf))
    }

    private fun initView() {
        // 初始化 ViewPager
        mDataBinding.viewPager.apply {
            adapter = MainPagerAdapter(supportFragmentManager, mFragmentFactory)
            offscreenPageLimit = 3
        }

        // 初始化 bottom bar
        mDataBinding.bottomBar.apply {
            setupWithViewPager(mDataBinding.viewPager)
            // 设置选中监听
            addOnTabSelectedListener(mTabSelectedListener)
            // 重置 Tab 按钮
            for (i in 0 until tabCount) {
                    getTabAt(i)!!.customView = mFragmentFactory.createFragmentTabView(
                        context, i
                    )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.meun_main, menu)
        menu?.forEach { item ->
            if (mViewModel.curPagePos.get() == 2) {
                item.isVisible = false
            }
        }
        return true
    }

    private val mTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabReselected(p0: TabLayout.Tab?) {
        }

        override fun onTabUnselected(p0: TabLayout.Tab?) {
        }

        override fun onTabSelected(p0: TabLayout.Tab?) {
            // 设置当前显示的索引
            mViewModel.curPagePos.set(mDataBinding.bottomBar.selectedTabPosition)

            // 设置标题
            mViewModel.curPageTitle.set(
                mFragmentFactory.getFragmentTitle(
                    baseContext,
                    mDataBinding.bottomBar.selectedTabPosition
                )
            )

            // 刷新菜单
            invalidateOptionsMenu()
        }
    }
}
