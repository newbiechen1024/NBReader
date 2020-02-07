package com.newbiechen.nbreader.ui.component.book

import android.content.Context
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.local.room.dao.BookDao
import com.newbiechen.nbreader.ui.component.book.plugin.BookPluginManager
import com.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

/**
 *  author : newbiechen
 *  date : 2019-09-16 11:13
 *  description :书籍管理器
 *
 *  1. 需要获取书籍信息
 *  2. 需要重新进行解析
 *  3. 需要重新绘制
 *
 *  TODO:应该添加书籍加载错误的监听回调
 */

/**
 * @param bookDao：书籍数据库
 * @param textProcessor:文本处理器
 */
class BookManager constructor(
    private val bookDao: BookDao,
    private val textProcessor: TextProcessor
) {
    companion object {
        private const val TAG = "BookManager"
    }

    private var mBookListener: OnBookListener? = null

    fun setOnBookListener(bookListener: OnBookListener) {
        mBookListener = bookListener
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
    // 打开书籍
    fun openBook(context: Context, book: BookEntity) {
        mBookListener?.onLoading()

        // TODO:暂时这么用吧，找不到好的创建线程池的方法。==> 不过应该有一个更好的处理这个问题
        Observable.create(ObservableOnSubscribe<Int> {
            try {
                openBookInternal(context, book)
            } catch (e: Exception) {
                it.onError(e)
            }

            it.onNext(0)

            it.onComplete()
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                Consumer {
                    mBookListener?.onLoadSuccess()
                },
                Consumer {
                    mBookListener?.onLoadFailure(it)
                }
            )
    }

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

    private fun openBookInternal(context: Context, book: BookEntity) {
        val pluginManager = BookPluginManager.getInstance(context)
        // 根据 Book 获取到 Plugin
        val plugin = pluginManager.getPlugin(book.type)
            ?: throw IllegalAccessException("UnSupport Book Type")

        val cachePath = NativeFormatPlugin.getBookCacheDir(context, book)
        val chapterPattern =
            "^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$"

        val prologueTitle = "序章"

        // 设置配置参数
        plugin.setConfigure(cachePath, chapterPattern, prologueTitle)

        // 传入书籍资源
        plugin.setBookResource(book.url)

        // TODO:BookManager 不应该直接获取到 TextProcessor，这个可以之后处理，连同传入 NativePlugin 一起修改了

        // 对文本处理器，设置文本模块
        textProcessor.setTextResource(plugin)
    }
}

interface OnBookListener {
    // 加载书籍
    fun onLoading()

    // 加载书籍成功
    fun onLoadSuccess()

    // 加载书籍失败
    fun onLoadFailure(e: Throwable)

}