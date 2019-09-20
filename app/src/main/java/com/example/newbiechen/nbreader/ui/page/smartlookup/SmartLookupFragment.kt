package com.example.newbiechen.nbreader.ui.page.smartlookup

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.FragmentSmartLookupBinding
import com.example.newbiechen.nbreader.ui.component.adapter.SmartLookupAdapter
import com.example.newbiechen.nbreader.ui.component.decoration.PinnedHeaderItemDecoration
import com.example.newbiechen.nbreader.ui.page.filesystem.IFileSystem
import com.example.newbiechen.nbreader.ui.page.filesystem.IFileSystemCallback
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment

/**
 *  author : newbiechen
 *  date : 2019-08-17 12:34
 *  description :
 */

class SmartLookupFragment : BaseBindingFragment<FragmentSmartLookupBinding>(), IFileSystem {
    private lateinit var mViewModel: SmartLookupViewModel
    private lateinit var mAdapter: SmartLookupAdapter

    companion object {
        private const val TAG = "SmartLookupFragment"
    }

    private var mFileSystemCallback: IFileSystemCallback? = null

    override fun initContentView(): Int = R.layout.fragment_smart_lookup

    override fun initView() {
        super.initView()
        mAdapter = SmartLookupAdapter().apply {
            setOnCheckedChangeListener { _, isChecked ->
                mFileSystemCallback?.onCheckedChange(isChecked)
            }
        }

        mDataBinding.rvBookInfo.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            addItemDecoration(PinnedHeaderItemDecoration())
        }
    }

    override fun processLogic() {
        mViewModel = ViewModelProviders.of(this).get(SmartLookupViewModel::class.java)
        mDataBinding.viewModel = mViewModel
        // 加载本地书籍信息
        mViewModel.loadLocalBookInfo(activity!!)
    }

    override fun setCheckedAll(isChecked: Boolean) {
        if (mAdapter == null) return
        mAdapter.setCheckedAll(isChecked)
    }

    override fun getCheckedCount(): Int {
        if (mAdapter == null) return 0
        return mAdapter.getCheckedCount()
    }

    override fun deleteCheckedAll() {
        var checkedBookIds = mAdapter.getCheckedBookInfo().map { it.id }
        mViewModel.deleteBookInfos(checkedBookIds)
    }

    override fun getCheckedFile(): List<String> {
        return mAdapter.getCheckedBookInfo().map { it.path }
    }

    override fun setFileCallback(callback: IFileSystemCallback) {
        mFileSystemCallback = callback
    }

    override fun getFileCount(): Int {
        return mAdapter.getGroupChildCount()
    }
}
