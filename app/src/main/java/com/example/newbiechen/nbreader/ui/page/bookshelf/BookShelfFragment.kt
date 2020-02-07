package com.example.newbiechen.nbreader.ui.page.bookshelf

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.FragmentBookShelfBinding
import com.example.newbiechen.nbreader.ui.component.adapter.BookShelfAdapter
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingFragment
import com.example.newbiechen.nbreader.ui.page.read.ReadActivity
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import javax.inject.Inject

class BookShelfFragment : BaseBindingFragment<FragmentBookShelfBinding>() {

    companion object {
        private const val TAG = "BookShelfFragment"
        fun newInstance() = BookShelfFragment()
    }

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    private lateinit var mViewModel: BookShelfViewModel

    override fun initContentView(): Int = R.layout.fragment_book_shelf

    override fun initView() {
        super.initView()
        initAdapter()
    }

    override fun processLogic() {
        mViewModel = ViewModelProvider(this, mViewModelFactory)
            .get(BookShelfViewModel::class.java)

        mDataBinding.viewModel = mViewModel
        // 加载缓存书籍信息
        mViewModel.loadCacheBooks()
    }

    private fun initAdapter() {
        val bookAdapter = BookShelfAdapter()
        // 初始化 book 的 recyclerView
        mDataBinding.rvBook.apply {

            layoutManager = LinearLayoutManager(context)

            adapter = LRecyclerViewAdapter(bookAdapter).apply {
                setOnItemClickListener { _, position ->
                    val book = bookAdapter.getItem(position)!!
                    // 设置点击事件
                    ReadActivity.startActivity(context, book)
                }
            }

            // 禁止刷新
            setPullRefreshEnabled(false)

            // 禁止加载更多
            setLoadMoreEnabled(false)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
}