package com.newbiechen.nbreader.ui.page.find

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.newbiechen.nbreader.R

import com.newbiechen.nbreader.databinding.FragmentFindBinding
import com.newbiechen.nbreader.ui.component.adapter.FindAdapter
import com.newbiechen.nbreader.uilts.factory.ViewModelFactory

import com.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import javax.inject.Inject
import com.newbiechen.nbreader.ui.component.decoration.SpaceDecoration
import com.newbiechen.nbreader.ui.page.booklist.BookListActivity


class FindFragment : BaseBindingFragment<FragmentFindBinding>() {
    override fun initContentView(): Int = R.layout.fragment_find

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mViewModel: FindViewModel

    companion object {
        private const val TAG = "FindFragment"
        fun newInstance() = FindFragment()
    }

    override fun initView() {
        mViewModel = ViewModelProvider(this, viewModelFactory).get(FindViewModel::class.java)
        mDataBinding.apply {
            viewModel = mViewModel
            rvBookCatalog.apply {
                layoutManager = GridLayoutManager(activity, 3)
                addItemDecoration(SpaceDecoration(verticalSpace = context.resources.getDimensionPixelSize(R.dimen.item_find_space)))
                adapter = FindAdapter().apply {
                    setOnItemClickListener { _, value ->
                        BookListActivity.startActivity(context, value)
                    }
                }
            }
        }
    }

    override fun processLogic() {
        mViewModel.loadCatalog()
    }
}