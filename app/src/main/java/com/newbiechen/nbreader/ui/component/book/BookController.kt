package com.newbiechen.nbreader.ui.component.book

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.ui.component.book.text.processor.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PageProgress
import com.newbiechen.nbreader.ui.component.widget.page.PageController
import com.newbiechen.nbreader.ui.component.widget.page.PageType
import com.newbiechen.nbreader.uilts.FileUtil
import com.newbiechen.nbreader.uilts.Void
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.lang.Exception

/**
 *  author : newbiechen
 *  date : 2019-09-16 11:13
 *  description :书籍控制器
 */

/**
 * @param pageController:页面控制器
 */
class BookController constructor(
    private val pageController: PageController
) {

    companion object {
        private const val TAG = "BookController"
        // 章节匹配
        private const val CHAPTER_PATTERN =
            "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$"
        private const val INIT_CHAPTER_TITLE = "开始"
    }

    private var mHandlerThread =
        HandlerThread("com.newbiechen.nbreader.ui.component.book.bookcontroller").apply {
            start()
        }

    private var mHandler = Handler(mHandlerThread.looper)

    private var mLoadListener: OnLoadListener? = null

    fun setOnLoadListener(loadListener: OnLoadListener) {
        mLoadListener = loadListener
    }

    // 打开书籍
    fun open(context: Context, book: BookEntity): Disposable {
        mLoadListener?.onLoading()

        return Single.create<Void> {
            try {
                openBookInternal(context, book)
            } catch (e: Exception) {
                it.onError(e)
            }
            it.onSuccess(Void())
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { _ ->
                    // 成功回调
                    mLoadListener?.onLoadSuccess()
                },
                { error ->
                    // 错误回调
                    mLoadListener?.onLoadFailure(error)
                }
            )
    }

    /**
     * 跳转章节
     */
    fun skipChapter(type: PageType) {
        // TODO:由于skipChapter 操作耗时，所以需要异步处理
        // TODO:逻辑应该在 TextPageController 中处理这个问题，暂时放这里处理
        mHandler.post {
            pageController.skipChapter(type)
        }
    }

    /**
     * 跳转章节
     */
    fun skipChapter(index: Int) {
        // TODO:由于skipChapter 操作耗时，所以需要异步处理
        mHandler.post {
            pageController.skipChapter(index)
        }
    }

    private fun openBookInternal(context: Context, book: BookEntity) {
        // 获取书籍缓存路径
        val cachePath = FileUtil.getCachePath(context) + File.separator + book.id
        // 配置参数
        pageController.setConfigure(cachePath, INIT_CHAPTER_TITLE, CHAPTER_PATTERN)
        // 打开书籍
        pageController.open(book.url, book.type)
    }

    /**
     * 获取章节列表
     */
    fun getChapters(): List<Chapter> {
        // 检测是否打开书籍
        check(pageController.isOpen()) {
            "Please open book before read chapters"
        }
        // 将章节数据转换成章节
        return pageController.getChapters().mapIndexed { index, textChapter ->
            Chapter(index, textChapter.title)
        }
    }

    /**
     * 获取当前的定位
     */
    fun getCurPosition(): PagePosition? {
        return pageController.getCurPosition()
    }

    fun getCurProgress(): PageProgress? {
        return pageController.getCurProgress()
    }

    fun close() {
        // 停止线程
        mHandlerThread.quit()
        // 关闭控制器
        pageController.close()
    }
}

data class Chapter(
    val index: Int,
    val title: String
)

interface OnLoadListener {
    // 加载书籍
    fun onLoading()

    // 加载书籍成功
    fun onLoadSuccess()

    // 加载书籍失败
    fun onLoadFailure(e: Throwable)
}

/**
 * FBReader 的实现步骤：
 *
 * 1. 判断当前是否已经存在打开的书籍，如果与当前打开的书籍一致，则直接返回。
 * 2. 如果传入的书籍为空，则从 SyncData 中获取最近阅读过的书籍。SyncData 实际走的是 SharePreference
 * 3. 如果还为空，则从 LibraryService 中获取最近阅读的书籍。LibraryService 走的是书籍库。
 * 4. 关于 2 ~ 3 不太清楚为什么要搞多级缓存，以后做到了再思考吧。
 * 5. 如果还为空，则打开帮助页面。
 * 6. 为 Book 设置标签 ==> 暂时不知道标签有什么用
 * 7. 将 Book 存储到 LibraryService 中
 * 8. 调用 openBookInternal 执行实际打开书本操作
 */

/**
 * FBReader 的实现步骤：
 *
 * 1. 删除旧 Book 数据，并调用 GC 回收
 * 2. 创建书籍插件对象，并根据 book 获取对应类型的插件
 * 3. 根据不同的插件类型，执行不同的逻辑，
 * 4. 通过 Plugin 实例化 BookModel
 * 5. 将 BookModel 赋值给 PageController
 * 6. 设置高亮，并跳转到指定位置
 * 7. 利用 LibraryService 设置为最近阅读书籍
 * 8. 通知 PageView 进行重绘
 */