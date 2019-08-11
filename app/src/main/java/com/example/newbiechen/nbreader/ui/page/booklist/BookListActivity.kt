package com.example.newbiechen.nbreader.ui.page.booklist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.CatalogEntity
import com.example.newbiechen.nbreader.databinding.ActivityBookListBinding
import com.example.newbiechen.nbreader.ui.component.adapter.BookListAdapter
import com.example.newbiechen.nbreader.ui.component.adapter.BookListFilterAdapter
import com.example.newbiechen.nbreader.ui.component.adapter.BookListSortAdapter
import com.example.newbiechen.nbreader.ui.component.decoration.SpaceItemDecoration
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.github.jdsjlzx.ItemDecoration.SpacesItemDecoration
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter
import com.github.jdsjlzx.view.LoadingFooter
import com.youtubedl.ui.main.base.BaseBindingActivity
import javax.inject.Inject

// 书籍列表页面

class BookListActivity : BaseBindingActivity<ActivityBookListBinding>() {

    @Inject
    lateinit var mViewModelFactory: ViewModelFactory

    private lateinit var mCatalogEntity: CatalogEntity
    private lateinit var mViewModel: BookListViewModel

    private lateinit var mFilterAdapter: BookListFilterAdapter

    // tag title
    private lateinit var mLabelTagTitle: String
    private lateinit var mStatusTagTitle: String
    private lateinit var mUpdateTagTitle: String

    // 状态 tag 列表
    private lateinit var mStatusTagList: List<String>

    // 书籍排序列表
    private lateinit var mSortList: List<String>

    // 更新时间 tag 列表
    private val mUpdateTagList = listOf(3, 7, 15, 30)

    // 当前选中的 tag 记录
    private var mLastSelectedTagMap: Map<String, List<Int>>? = null
    // 当前选中的 sort 记录
    private var mSelectedSort: Int = 0

    // tag 是否改变
    private var isTagChange = false

    companion object {
        private const val EXTRA_CATALOG = "extra_catalog"
        private const val TAG = "BookListActivity"

        fun startActivity(context: Context, catalogEntity: CatalogEntity) {
            val intent = Intent(context, BookListActivity::class.java)
            intent.putExtra(EXTRA_CATALOG, catalogEntity)
            context.startActivity(intent)
        }
    }

    override fun initContentView(): Int = R.layout.activity_book_list

    override fun initData(savedInstanceState: Bundle?) {
        mCatalogEntity = intent.getParcelableExtra(EXTRA_CATALOG)

        mLabelTagTitle = getString(R.string.common_catalog)
        mStatusTagTitle = getString(R.string.common_status)
        mUpdateTagTitle = getString(R.string.common_update_time)

        mStatusTagList = listOf(
            getString(R.string.common_serial), getString(R.string.common_complete)
        )

        mSortList = listOf(
            getString(R.string.common_popularity),
            getString(R.string.common_remain),
            getString(R.string.common_comment_rate),
            getString(R.string.common_word_count)
        )
    }

    override fun initView() {
        initToolbar()
        initDrawerLayout()
        initBookList()
        initBookFilter()
        initBookSort()

        mDataBinding.apply {
            tvFilter.setOnClickListener {
                // 展开侧滑栏
                dlSlide.openDrawer(Gravity.RIGHT, true)
            }
        }
    }

    private fun initToolbar() {
        // 初始化 toolbar
        mDataBinding.toolbarInclude.apply {
            setSupportActionBar(toolbar)
            // 隐藏标题
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            // 设置回退
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            // 设置标题
            tvTitle.text = mCatalogEntity.name
        }
    }

    private fun initDrawerLayout() {
        mDataBinding.dlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        mDataBinding.dlSlide.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
                // 如果 tag 改变时关闭,需要进行还原
                if (isTagChange) {
                    mFilterAdapter.reset(mLastSelectedTagMap)
                }

                isTagChange = false
            }
        })
    }

    private fun initBookList() {
        val bookAdapter = BookListAdapter()

        // 初始化 book 的 recyclerView
        mDataBinding.rvBook.apply {
            val itemSpace = context.resources.getDimensionPixelSize(R.dimen.space_item_book_list)
            layoutManager = GridLayoutManager(context, 3)

            adapter = LRecyclerViewAdapter(bookAdapter).apply {
                setOnItemClickListener { _, position ->
                    LogHelper.i(TAG, "onItemClick:$position")
                }
            }
            addItemDecoration(SpaceItemDecoration(itemSpace, itemSpace))

            setPullRefreshEnabled(false)
            // 设置点击事件
            // 加载更多监听
            setOnLoadMoreListener {
                LogHelper.i(TAG, "setOnLoadMoreListener")
            }
        }
    }

    private fun initBookFilter() {
        mFilterAdapter = BookListFilterAdapter().apply {
            addFilterTagGroup(mLabelTagTitle, mCatalogEntity.labels, true)
            addFilterTagGroup(mStatusTagTitle, mStatusTagList, false)
            addFilterTagGroup(
                mUpdateTagTitle,
                mUpdateTagList.map { getString(R.string.during_day, it) },
                false
            )

            setOnTagChangeListener { key, value ->
                // tag 改变了
                isTagChange = true
            }
        }

        mDataBinding.rvFilter.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = mFilterAdapter
        }

        mDataBinding.tvReset.setOnClickListener {
            // 重置
            mFilterAdapter.reset()
            // 通知 tag 改变
            isTagChange = true
        }

        mDataBinding.tvSure.setOnClickListener {
            // 如果没有改变，则 sure 点击无效
            if (!isTagChange) {
                return@setOnClickListener
            }

            // 获取选中的 tag
            mLastSelectedTagMap = mFilterAdapter.getFilterResult()

            // 设置 tag 未改变，防止 drawer 重置
            isTagChange = false

            // 关闭 drawer
            mDataBinding.dlSlide.closeDrawer(Gravity.RIGHT, true)

            // 刷新 ViewModel
            refreshBookList()
        }
    }

    private fun initBookSort() {
        mDataBinding.rvBookSort.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            addItemDecoration(BookListSortAdapter.BookListSortDecoration())

            adapter = BookListSortAdapter().apply {
                refreshItems(mSortList)
                setOnItemClickListener { pos, _ ->
                    // 更新数据
                    mSelectedSort = pos
                    refreshBookList()
                }
            }
        }
    }

    override fun processLogic() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(BookListViewModel::class.java)
        mDataBinding.viewModel = mViewModel
        // 请求书籍信息
        refreshBookList()
    }

    private fun refreshBookList() {
        if (mLastSelectedTagMap != null) {
            // 获取标签
            var cat: String? = null
            mLastSelectedTagMap!![mLabelTagTitle]?.map {
                // 根据索引获取对应的 label
                mCatalogEntity.labels[it]
            }?.forEach {
                // 将标签使用 , 分隔符连接
                cat += "$it,"
            }
            // 删除最后的, 分隔符
            cat = cat?.replaceAfterLast(",", "")

            // 获取书籍状态
            var isserial: Boolean? = null
            var isserialResult = mLastSelectedTagMap!![mStatusTagTitle]?.get(0)
            if (isserialResult != null) {
                isserial = isserialResult == 0
            }

            var update: Int? = null

            // 获取更新时间
            var updateResult = mLastSelectedTagMap!![mUpdateTagTitle]?.get(0)
            if (updateResult != null) {
                update = mUpdateTagList[updateResult!!]
            }

            mViewModel.refreshBookList(
                this, mCatalogEntity.alias,
                mSelectedSort, cat, isserial, update
            )

        } else {
            mViewModel.refreshBookList(this, mCatalogEntity.alias, mSelectedSort)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}