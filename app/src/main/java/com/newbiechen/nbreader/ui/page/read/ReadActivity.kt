package com.newbiechen.nbreader.ui.page.read

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.databinding.ActivityReadBinding
import com.newbiechen.nbreader.ui.component.adapter.PageAnimAdapter
import com.newbiechen.nbreader.ui.component.adapter.ReadCatalogAdapter
import com.newbiechen.nbreader.ui.component.book.BookController
import com.newbiechen.nbreader.ui.component.book.OnLoadListener
import com.newbiechen.nbreader.ui.component.book.text.engine.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.engine.PageProgress
import com.newbiechen.nbreader.ui.component.decoration.DividerDecoration
import com.newbiechen.nbreader.ui.component.decoration.SpaceDecoration
import com.newbiechen.nbreader.ui.component.extension.closeDrawer
import com.newbiechen.nbreader.ui.component.extension.isDrawerOpen
import com.newbiechen.nbreader.ui.component.extension.openDrawer
import com.newbiechen.nbreader.ui.component.widget.page.DefaultActionCallback
import com.newbiechen.nbreader.ui.component.widget.page.OnPageListener
import com.newbiechen.nbreader.ui.component.widget.page.PageAnimType
import com.newbiechen.nbreader.ui.component.widget.page.action.TapMenuAction
import com.newbiechen.nbreader.uilts.SystemBarUtil
import com.newbiechen.nbreader.ui.page.base.BaseBindingActivity

/**
 *  author : newbiechen
 *  date : 2019-08-25 16:28
 *  description :书籍阅读页面
 */

class ReadActivity : BaseBindingActivity<ActivityReadBinding>(), View.OnClickListener {

    companion object {
        private const val TAG = "ReadActivity"
        private const val EXTRA_BOOK = "extra_book"

        fun startActivity(context: Context, book: BookEntity) {
            val intent = Intent(context, ReadActivity::class.java)
            intent.putExtra(EXTRA_BOOK, book)
            context.startActivity(intent)
        }
    }

    private lateinit var mViewModel: ReadViewModel

    private lateinit var mBook: BookEntity

    private lateinit var mBookController: BookController

    private lateinit var mCatalogAdapter: ReadCatalogAdapter

    private lateinit var mTvPageTitle: TextView

    private lateinit var mTvPageTip: TextView

    override fun initContentView(): Int = R.layout.activity_read

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mBook = intent.getParcelableExtra(EXTRA_BOOK)
    }

    override fun initView() {
        super.initView()
        mDataBinding.apply {
            // 初始化 toolbar
            supportActionBar(toolbar)
            // 设置颜色半透明
            overStatusBar(toolbar)
            // 隐藏系统状态栏
            hideSystemBar()

            // 初始化点击事件
            tvCategory.setOnClickListener(this@ReadActivity)
            tvNightMode.setOnClickListener(this@ReadActivity)
            tvSetting.setOnClickListener(this@ReadActivity)
            tvBright.setOnClickListener(this@ReadActivity)
            menuFrame.setOnClickListener(this@ReadActivity)

            initPageView()

            initSlideView()

            initMenu()
        }
    }

    /**
     * 初始化侧滑栏
     */
    private fun initSlideView() {
        mDataBinding.apply {
            // 初始化侧滑栏
            dlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

            // 允许手势关闭侧滑栏
            dlSlide.addDrawerListener(object : DrawerLayout.DrawerListener {
                override fun onDrawerStateChanged(newState: Int) {
                }

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                }

                override fun onDrawerClosed(drawerView: View) {
                    dlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }

                override fun onDrawerOpened(drawerView: View) {
                    dlSlide.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                    // 让状态栏消失
                    hideSystemBar()
                }
            })

            // 设置背景图片
            val slideBg = Drawable.createFromStream(assets.open("wallpaper/paper.jpg"), null)

            llSlideContainer.background = slideBg

            // 初始化目录标题
            tvBookTitle.text = mBook.title

            // 初始化 RecyclerView
            mCatalogAdapter = ReadCatalogAdapter()

            mCatalogAdapter.setOnItemClickListener { _, value ->
                // 通知切换章节
                mBookController.skipChapter(value.index)
                // 关闭滑动
                dlSlide.closeDrawer()
            }

            rvCatalog.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = mCatalogAdapter
                addItemDecoration(
                    DividerDecoration(
                        dividerColor = context.resources.getColor(R.color.read_catalog_divider)
                    )
                )
            }
        }
    }

    private fun initPageView() {
        val pageView = mDataBinding.pvBook

        val pageHeaderView =
            LayoutInflater.from(this).inflate(R.layout.layout_page_header, pageView, false)
        mTvPageTitle = pageHeaderView.findViewById(R.id.tv_title)
        val pageFooterView =
            LayoutInflater.from(this).inflate(R.layout.layout_page_footer, pageView, false)
        mTvPageTip = pageFooterView.findViewById(R.id.tv_page_tip)
        pageView.apply {
            // 设置顶部和底部
            setHeaderView(pageHeaderView)
            setFooterView(pageFooterView)

            // 将页面控制器，封装为书籍控制器
            mBookController = BookController(getPageController())

            // 设置页面监听
            mBookController.setPageListener(object : OnPageListener {
                override fun onPreparePage(pagePosition: PagePosition, pageProgress: PageProgress) {
                    // TODO:异步加载书籍，会导致异步获取到了章节，但是还没有发送给 catalogAdapter 就回调了。
                    if (mCatalogAdapter.getItem(pagePosition.chapterIndex) == null) {
                        return
                    }

                    // TODO:使用 DataBinding 会导致数据更新不及时的问题，所以 headerView 和 footerView 禁止使用
                    onPagePositionChange(pagePosition, pageProgress)
                }

                override fun onPageChange(pagePosition: PagePosition, pageProgress: PageProgress) {
                    onPagePositionChange(pagePosition, pageProgress)
                }
            })

            // 设置页面事件监听
            mBookController.setPageActionListener(object : DefaultActionCallback() {
                override fun onTapMenuAction(action: TapMenuAction) {
                    toggleMenu()
                }
            })
        }
    }

    /**
     * 初始化菜单
     */
    private fun initMenu() {
        // 初始化翻页动画
        mDataBinding.rvReadAnim.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = PageAnimAdapter().also {
                // 添加数据
                it.refreshItems(PageAnimType.values().toList())

                // 设置点击事件监听
                it.setOnItemClickListener { pos, value ->
                    mDataBinding.pvBook.setPageAnim(value)
                }
            }

            addItemDecoration(
                SpaceDecoration(
                    horizonSpace = context.resources.getDimension(R.dimen.space_read_setting_page_anim)
                        .toInt(), isBetween = true
                )
            )
        }
    }

    private fun onPagePositionChange(pagePosition: PagePosition, pageProgress: PageProgress) {
        // 设置顶部信息
        mTvPageTitle.text = resources.getString(
            R.string.read_chapter_title,
            mCatalogAdapter.getItem(pagePosition.chapterIndex)!!.title
        )

        // 设置底部信息
        mTvPageTip.text = resources.getString(
            R.string.read_page_tip, pageProgress.pageIndex + 1, pageProgress.pageCount
        )
    }

    private fun showSystemBar() {
        //显示
        SystemBarUtil.showUnStableStatusBar(this)
    }

    private fun hideSystemBar() {
        //隐藏
        SystemBarUtil.hideStableStatusBar(this)
        // SystemBarUtil.hideStableNavBar(this)
    }

    override fun processLogic() {
        super.processLogic()
        mViewModel = ViewModelProvider(this).get(ReadViewModel::class.java)
        // 需要对 viewmodel 做一次初始化操作
        mViewModel.init(this)
        mDataBinding.viewModel = mViewModel

        // 打开书籍
        openBook()
    }

    private fun openBook() {
        // TODO:需要有加载完成动画

        var loadDialog: ProgressDialog? = null

        mBookController.setOnLoadListener(object : OnLoadListener {

            override fun onLoading() {
                // 弹出一个 Loading Dialog
                loadDialog = ProgressDialog.show(
                    this@ReadActivity, "等待书籍加载完成",
                    "Loading. Please wait...", true
                )
            }

            override fun onLoadSuccess() {
                // 关闭 loading Dialog
                loadDialog!!.cancel()
                // 显示章节信息
                mCatalogAdapter.refreshItems(mBookController.getChapters())

                // 更新 page 信息
                val pageProgress = mBookController.getCurProgress()
                val pagePosition = mBookController.getCurPosition()

                if (pageProgress != null && pagePosition != null) {
                    onPagePositionChange(pagePosition, pageProgress)
                }

                // 刷新页面
                mDataBinding.executePendingBindings()
            }

            override fun onLoadFailure(e: Throwable) {
                // 关闭 loading Dialog
                loadDialog!!.cancel()
            }
        })

        addDisposable(mBookController.open(this, mBook))
    }

    override fun onBackPressed() {
        when {
            mDataBinding.dlSlide.isDrawerOpen() -> mDataBinding.dlSlide.closeDrawer()
            mViewModel.isShowMenu.get()!! -> mViewModel.isShowMenu.set(false)
            else -> super.onBackPressed()
        }
    }

    private fun toggleMenu() {
        mViewModel.apply {
            if (isShowMenu.get()!!) {
                hideSystemBar()
            } else {
                showSystemBar()
            }

            isShowMenu.set(!isShowMenu.get()!!)
        }
    }

    private fun toggleNightMode() {
        mViewModel.apply {
            isNightMode.set(!isNightMode.get()!!)
        }
    }

    override fun onClick(v: View?) {
        mViewModel.apply {
            when (v!!.id) {
                R.id.tv_night_mode -> {
                    toggleNightMode()
                }
                R.id.tv_category -> {
                    // 关闭菜单
                    isShowMenu.set(false)
                    // 打开抽屉
                    mDataBinding.dlSlide.openDrawer()
                }
                R.id.tv_setting -> {
                    // 创建 dialog
                    isShowMenu.set(false)
                    isShowSettingMenu.set(true)
                }
                R.id.tv_bright -> {
                    isShowMenu.set(false)
                    isShowBrightMenu.set(true)
                }
                R.id.menu_frame -> {
                    when {
                        isShowSettingMenu.get()!! -> isShowSettingMenu.set(false)
                        isShowBrightMenu.get()!! -> isShowBrightMenu.set(false)
                        else -> toggleMenu()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭书籍
        mBookController.close()
    }
}