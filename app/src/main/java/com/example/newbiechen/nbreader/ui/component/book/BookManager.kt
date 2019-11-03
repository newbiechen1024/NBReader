package com.example.newbiechen.nbreader.ui.component.book

import android.content.Context
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.data.local.room.dao.BookDao
import com.example.newbiechen.nbreader.ui.component.book.plugin.BookPluginManager
import com.example.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.example.newbiechen.nbreader.ui.component.widget.page.PageController
import com.example.newbiechen.nbreader.uilts.LogHelper
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

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

class BookManager constructor(private val bookDao: BookDao) {


    companion object {
        private const val TAG = "BookManager"
    }

    private lateinit var mPageController: PageController
    private var mBookModel: BookModel? = null

    /**
     * 需要加载页面控制器
     */
    fun initPageController(controller: PageController) {
        mPageController = controller
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
        // TODO:暂时这么用吧，找不到好的创建线程池的方法。==> 不过应该有一个更好的处理这个问题
        Observable.create(ObservableOnSubscribe<Int> {
            openBookInternal(context, book)
            it.onNext(0)
            it.onComplete()
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                LogHelper.i(TAG, "openBookInternal success")
            }
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
        val plugin = pluginManager.getPlugin(book.type) ?: throw IllegalAccessException("UnSupport Book Type")
        // 根据 Book 实例化
        mBookModel = BookModel.createBookModel(book, plugin as NativeFormatPlugin)
        // 将生成的 textModel 赋值给 controller
        mPageController.setBookModel(mBookModel!!)
    }
}