package com.example.newbiechen.nbreader.ui.page.bookdetail

import android.content.Context
import androidx.databinding.ObservableField
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookDetailWrapper
import com.example.newbiechen.nbreader.data.repository.impl.IBookDetailRepository
import com.example.newbiechen.nbreader.ui.component.widget.StatusView
import com.example.newbiechen.nbreader.ui.page.base.RxViewModel
import com.example.newbiechen.nbreader.uilts.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormat
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-14 18:27
 *  description :
 */

class BookDetailViewModel @Inject constructor(private val repository: IBookDetailRepository) : RxViewModel() {

    companion object {
        private const val TAG = "BookDetailViewModel"
    }

    // 是否显示状态页
    val isVisibleStatusPage = ObservableField<Boolean>()
    // 状态页面的状态
    val pageStatus = ObservableField<Int>()
    val bookDetail = ObservableField<BookDetailBean>()

    fun loadBookDetail(context: Context, bookId: String) {
        isVisibleStatusPage.set(true)

        // 检测当前网络状态
        if (!NetworkUtil.isNetworkAvaialble(context)) {
            pageStatus.set(StatusView.STATUS_ERROR)
            return
        }

        pageStatus.set(StatusView.STATUS_LOADING)

        compositeDisposable.add(
            repository.getBookDetail(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val imgUrl = Constants.IMG_BASE_URL + it.cover
                        val type = "${it.majorCate} • ${it.minorCate}"

                        val categoryBrief = context.getString(
                            R.string.book_detail_category_brief,
                            it.chaptersCount, DateUtil.dateConvert(it.updated, DateUtil.FORMAT_FILE_DATE)
                        )

                        val word = context.getString(
                            R.string.common_word,
                            NumberUtil.convertNumber(context, it.wordCount.toLong())
                        )

                        val bookDetailBean = BookDetailBean(
                            imgUrl,
                            it.title,
                            it.author,
                            type,
                            it.longIntro,
                            (it.rating.score / 2).toInt(),
                            categoryBrief,
                            it.tags,
                            word,
                            it.isSerial,
                            NumberUtil.convertNumber(context, it.latelyFollower.toLong()),
                            context.getString(R.string.common_percent, it.retentionRatio)
                        )

                        LogHelper.i(TAG, "data:$bookDetailBean")

                        bookDetail.set(bookDetailBean)
                    }, {
                        pageStatus.set(StatusView.STATUS_ERROR)
                        LogHelper.i(TAG, "error:$it")
                    }, {
                        isVisibleStatusPage.set(false)
                        pageStatus.set(StatusView.STATUS_FINISH)
                    }
                )
        )
    }
}

data class BookDetailBean(
    val imgUrl: String,// 图片地址
    val title: String, // 标题
    val author: String, //作者
    val type: String, // 类型
    val brief: String, // 简介
    val score: Int, // 评分
    val categoryBrief: String, // 目录简介
    val labels: List<String>, // 标签
    val wordCount: String, // 总字数
    val isSerial: Boolean, // 是否连载
    val hot: String, // 人气
    val retentionRatio: String // 留存率
) {
    override fun toString(): String {
        return "BookDetailBean(imgUrl='$imgUrl', title='$title', author='$author', type='$type', brief='$brief', score=$score, categoryBrief='$categoryBrief', labels=$labels, wordCount='$wordCount', isSerial=$isSerial, hot='$hot', retentionRatio='$retentionRatio')"
    }

}