package com.newbiechen.nbreader.ui.page.filesystem

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.data.entity.LocalBookEntity
import com.newbiechen.nbreader.databinding.ActivityFileSystemBinding
import com.newbiechen.nbreader.ui.page.filecatalog.FileCatalogFragment
import com.newbiechen.nbreader.ui.page.smartlookup.SmartLookupFragment
import com.newbiechen.nbreader.ui.page.base.BaseBindingActivity
import com.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-16 18:27
 *  description :文件系统页面
 */

class FileSystemActivity : BaseBindingActivity<ActivityFileSystemBinding>() {

    private var mSmartLookupFrag = SmartLookupFragment.newInstance()

    private var mFileCatalogFrag = FileCatalogFragment.newInstance()

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    private lateinit var mCurFragment: Fragment
    private lateinit var mViewModel: FileSystemViewModel

    companion object {
        fun startActivity(context: Context) {
            AndPermission.with(context)
                .runtime()
                .permission(Permission.Group.STORAGE[0])
                .onGranted {
                    context.startActivity(Intent(context, FileSystemActivity::class.java))
                }
                .onDenied {
                    // 暂时不处理
                }
                .start()
        }
    }

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
        mViewModel = ViewModelProvider(this, mViewModelFactory).get(FileSystemViewModel::class.java)
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

        mDataBinding.apply {
            tvCheckedAll.setOnClickListener {
                mSmartLookupFrag.setBookCheckedAll(!mViewModel.isCheckedAll.get()!!)
                onCheckedChange()
            }

            tvDelete.setOnClickListener {
                mSmartLookupFrag.deleteCheckedBooks()
            }

            tvAddBookshelf.setOnClickListener {
                val fileSystem = mCurFragment as ILocalBookSystem
                // 通知存储选中的书籍
                fileSystem.saveCheckedBooks()
            }
        }

        mSmartLookupFrag.setBookCallback(object : ILocalBookCallback {
            override fun onCheckedChange(isChecked: Boolean) {
                onCheckedChange()
            }

            override fun onSaveCheckedBooks(localBooks: List<LocalBookEntity>) {
                mViewModel.saveBooks(localBooks)
                onCheckedChange()
            }

            override fun onDeleteCheckedBooks(localBooks: List<LocalBookEntity>) {
            }
        })
    }

    private fun onCheckedChange() {
        mViewModel.checkedCount.set(mSmartLookupFrag.getCheckedBookCount())

        // 是否全选
        if (mSmartLookupFrag.getCheckedBookCount() == mSmartLookupFrag.getBookCount()) {
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
        val tempFragment: Fragment = if (mCurFragment == mSmartLookupFrag) {
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