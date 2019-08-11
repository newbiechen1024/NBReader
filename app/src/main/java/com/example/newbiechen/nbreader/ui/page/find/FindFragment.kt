package com.example.newbiechen.nbreader.ui.page.find

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.example.newbiechen.nbreader.R

import com.example.newbiechen.nbreader.databinding.FragmentFindBinding
import com.example.newbiechen.nbreader.ui.component.adapter.FindAdapter
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory

import com.youtubedl.ui.main.base.BaseFragment
import javax.inject.Inject
import com.example.newbiechen.nbreader.ui.component.decoration.SpaceItemDecoration
import com.example.newbiechen.nbreader.ui.page.booklist.BookListActivity


class FindFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var mViewModel: FindViewModel

    companion object {
        private const val TAG = "FindFragment"

        fun newInstance() = FindFragment()
    }

    private lateinit var mDataBinding: FragmentFindBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(FindViewModel::class.java)
        mDataBinding = FragmentFindBinding.inflate(inflater, container, false).apply {
            viewModel = mViewModel
            rvBookCatalog.apply {
                layoutManager = GridLayoutManager(activity, 3)
                addItemDecoration(SpaceItemDecoration(verticalSpace = context.resources.getDimensionPixelSize(R.dimen.item_find_space)))
                adapter = FindAdapter().apply {
                    setOnItemClickListener { pos, value ->
                        BookListActivity.startActivity(context, value)
                    }
                }
            }
        }
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.loadCatalog()
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.dispose()
    }
}