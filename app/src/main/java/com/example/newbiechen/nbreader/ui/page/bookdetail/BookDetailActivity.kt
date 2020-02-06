package com.example.newbiechen.nbreader.ui.page.bookdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ActivityBookDetailBinding
import com.example.newbiechen.nbreader.ui.component.adapter.SimpleTagAdapter
import com.example.newbiechen.nbreader.uilts.SystemBarUtil
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.example.newbiechen.nbreader.uilts.glide.ScaleTransformation
import com.google.android.material.appbar.AppBarLayout
import com.example.newbiechen.nbreader.ui.page.base.BaseBindingActivity
import jp.wasabeef.glide.transformations.BlurTransformation
import javax.inject.Inject
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2019-08-12 19:59
 *  description :
 */

class BookDetailActivity : BaseBindingActivity<ActivityBookDetailBinding>() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var mViewModel: BookDetailViewModel
    private lateinit var mBookId: String

    companion object {
        private const val EXTRA_BOOK_ID = "extra_book_id"
        private const val TAG = "BookDetailActivity"

        fun startActivity(context: Context, bookId: String) {
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(EXTRA_BOOK_ID, bookId)
            context.startActivity(intent)

            val spanBuilder = SpannableStringBuilder()
            spanBuilder.append()
            spanBuilder.toString()
        }
    }

    override fun initContentView(): Int = R.layout.activity_book_detail

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mBookId = intent.getStringExtra(EXTRA_BOOK_ID)
    }

    override fun initView() {
        super.initView()
        // 显示超出状态栏
        overStatusBar()

        val expandStr = resources.getString(R.string.text_fold_tip)
        val bookTitleHeight = resources.getDimensionPixelSize(R.dimen.height_book_detail_title) * 2 / 3
        mDataBinding.apply {
            // 初始化 toolbar
            supportActionBar(toolbar)
            // 解决 toolbar 顶到 statusbar 的问题
            var toolbarMarginLayout: ViewGroup.MarginLayoutParams = toolbar.layoutParams as ViewGroup.MarginLayoutParams
            toolbarMarginLayout.topMargin = SystemBarUtil.getStatusBarHeight(this@BookDetailActivity)

            statusLayout.ivBack.setOnClickListener {
                finish()
            }

            // 设置折叠时的文本提示
            tvBookBrief.setFoldEllipsize(Html.fromHtml(expandStr))
            // 监听滑动，设置 title
            ablBookTitle.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, offset ->
                if (bookTitleHeight < abs(offset)) {
                    if (!tvTitle.isVisible) {
                        tvTitle.visibility = View.VISIBLE
                    }
                } else {
                    if (tvTitle.isVisible) {
                        tvTitle.visibility = View.GONE
                    }
                }
            })
        }
    }

    override fun processLogic() {
        super.processLogic()
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(BookDetailViewModel::class.java)
        mDataBinding.viewModel = mViewModel

        mViewModel.bookDetail.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val bookDetail = mViewModel.bookDetail.get()
                if (bookDetail != null) {
                    // 设置书籍的背景
                    Glide.with(this@BookDetailActivity)
                        .load(bookDetail.imgUrl)
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.ic_image_loading)
                        .error(R.drawable.ic_image_load_error)
                        .apply(
                            RequestOptions.bitmapTransform(
                                MultiTransformation(BlurTransformation(25, 10), ScaleTransformation(4.0f))
                            )
                        )
                        .into(mDataBinding.ivBookBg)

                    // 设置标签
                    mDataBinding.tflLabel.adapter = SimpleTagAdapter(bookDetail.labels)
                    mDataBinding.llLabel.visibility = if (bookDetail.labels.isNotEmpty()) View.VISIBLE else View.GONE
                }
            }
        })

        // 请求加载数据
        mViewModel.loadBookDetail(this, mBookId)
    }
}