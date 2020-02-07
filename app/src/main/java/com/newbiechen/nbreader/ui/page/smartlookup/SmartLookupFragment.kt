package com.newbiechen.nbreader.ui.page.smartlookup

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.data.entity.LocalBookEntity
import com.newbiechen.nbreader.databinding.FragmentSmartLookupBinding
import com.newbiechen.nbreader.ui.component.adapter.SmartLookupAdapter
import com.newbiechen.nbreader.ui.component.decoration.PinnedHeaderItemDecoration
import com.newbiechen.nbreader.ui.page.filesystem.ILocalBookSystem
import com.newbiechen.nbreader.ui.page.filesystem.ILocalBookCallback
import com.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import com.newbiechen.nbreader.uilts.factory.ViewModelFactory
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-17 12:34
 *  description :文件智能查找页
 */

class SmartLookupFragment : BaseBindingFragment<FragmentSmartLookupBinding>(), ILocalBookSystem {
    private lateinit var mAdapter: SmartLookupAdapter
    private lateinit var mViewModel: SmartLookupViewModel

    companion object {
        private const val TAG = "SmartLookupFragment"
        fun newInstance() = SmartLookupFragment()
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    private var mLocalBookCallback: ILocalBookCallback? = null

    override fun initContentView(): Int = R.layout.fragment_smart_lookup

    override fun initView() {
        super.initView()
        mAdapter = SmartLookupAdapter().apply {
            setOnCheckedChangeListener { _, isChecked ->
                mLocalBookCallback?.onCheckedChange(isChecked)
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
        mViewModel = ViewModelProvider(this, mViewModelFactory)
            .get(SmartLookupViewModel::class.java)

        mDataBinding.viewModel = mViewModel

        // 加载本地书籍信息
        mViewModel.loadLocalBooks(activity!!)
    }

    override fun setBookCheckedAll(isChecked: Boolean) {
        mAdapter.setCheckedAll(isChecked)
    }

    override fun getCheckedBookCount(): Int {
        return mAdapter.getCheckedCount()
    }

    override fun deleteCheckedBooks() {
        val checkedBookIds = mAdapter.getCheckedBooks().map { it.id }
        mViewModel.deleteLocalBooks(checkedBookIds)
    }

    override fun getCheckedBooks(): List<LocalBookEntity> {
        return mAdapter.getCheckedBooks()
    }

    override fun setBookCallback(callback: ILocalBookCallback) {
        mLocalBookCallback = callback
    }

    override fun getBookCount(): Int {
        return mAdapter.getGroupChildCount()
    }

    override fun saveCheckedBooks() {
        val localBooks = getCheckedBooks()
        mAdapter.saveCheckedBooks()
        mLocalBookCallback?.onSaveCheckedBooks(localBooks)
    }
}
