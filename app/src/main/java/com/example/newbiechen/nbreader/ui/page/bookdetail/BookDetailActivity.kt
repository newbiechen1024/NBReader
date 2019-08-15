package com.example.newbiechen.nbreader.ui.page.bookdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.lifecycle.ViewModelProviders
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ActivityBookDetailBinding
import com.example.newbiechen.nbreader.uilts.factory.ViewModelFactory
import com.youtubedl.ui.main.base.BaseBindingActivity
import javax.inject.Inject

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
        val expandStr = resources.getString(R.string.text_fold_tip)
        // 设置
        mDataBinding.tvBookBrief.setFoldEllipsize(Html.fromHtml(expandStr))
    }

    override fun processLogic() {
        super.processLogic()
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(BookDetailViewModel::class.java)
        mDataBinding.viewModel = mViewModel
        // 请求加载数据
        mViewModel.loadBookDetail(this, mBookId)
    }
}