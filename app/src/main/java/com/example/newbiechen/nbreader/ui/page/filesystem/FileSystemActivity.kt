package com.example.newbiechen.nbreader.ui.page.filesystem

import android.text.Html
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ActivityFileSystemBinding
import com.example.newbiechen.nbreader.ui.page.filecatalogy.FileCatalogFragment
import com.example.newbiechen.nbreader.ui.page.smartlookup.SmartLookupFragment
import com.youtubedl.ui.main.base.BaseBindingActivity

/**
 *  author : newbiechen
 *  date : 2019-08-16 18:27
 *  description :
 */

class FileSystemActivity : BaseBindingActivity<ActivityFileSystemBinding>() {

    private var mSmartLookupFrag = SmartLookupFragment()

    private var mFileCatalogFrag = FileCatalogFragment()

    private lateinit var mCurFragment: Fragment
    private lateinit var mViewModel: FileSystemViewModel

    override fun initContentView(): Int = R.layout.activity_file_system

    override fun initView() {
        super.initView()
        supportActionBar(mDataBinding.toolbar)
        initFragment()
        mDataBinding.tvTitle.setOnClickListener {
            toggleFragment()
        }
    }

    override fun processLogic() {
        super.processLogic()
        mViewModel = ViewModelProviders.of(this).get(FileSystemViewModel::class.java)
        mDataBinding.viewModel = mViewModel
    }

    private fun initFragment() {
        // 设置当前 fragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fl_content, mSmartLookupFrag)
            add(R.id.fl_content, mFileCatalogFrag)
            hide(mFileCatalogFrag)
            commit()
        }
        mCurFragment = mSmartLookupFrag

        supportActionBar!!.title = resources.getString(R.string.common_smart_import)

        mSmartLookupFrag.setFileCallback(object : IFileSystemCallback {
            override fun onCheckedChange(isChecked: Boolean) {
                onCheckedChange()
            }
        })

        mDataBinding.apply {
            tvCheckedAll.setOnClickListener {
                mSmartLookupFrag.setCheckedAll(!mViewModel.isCheckedAll.get()!!)
                onCheckedChange()
            }

            tvDelete.setOnClickListener {
                mSmartLookupFrag.deleteCheckedAll()
            }

            tvAddBookshelf.setOnClickListener {
                // 添加书籍
            }
        }
    }

    private fun onCheckedChange() {
        mViewModel.checkedCount.set(mSmartLookupFrag.getCheckedCount())

        // 是否全选
        if (mSmartLookupFrag.getCheckedCount() == mSmartLookupFrag.getFileCount()) {
            if (!mViewModel.isCheckedAll.get()!!) {
                mViewModel.isCheckedAll.set(true)
            }
        } else {
            if (mViewModel.isCheckedAll.get()!!) {
                mViewModel.isCheckedAll.set(false)
            }
        }
    }


    private fun toggleFragment() {
        val tempFragment = if (mCurFragment == mSmartLookupFrag) {
            supportActionBar!!.title = resources.getString(R.string.common_phone_catalog)
            mFileCatalogFrag
        } else {
            supportActionBar!!.title = resources.getString(R.string.common_smart_import)
            mSmartLookupFrag
        }

        supportFragmentManager.beginTransaction()
            .apply {
                show(tempFragment)
                hide(mCurFragment)
                commit()
            }

        mCurFragment = tempFragment
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_file_system, menu!!)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // 处理菜单的点击事件
        return super.onOptionsItemSelected(item)
    }
}