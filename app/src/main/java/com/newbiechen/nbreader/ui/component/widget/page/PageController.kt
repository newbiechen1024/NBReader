package com.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import com.newbiechen.nbreader.ui.component.book.plugin.BookGroup
import com.newbiechen.nbreader.ui.component.book.plugin.BookPluginFactory
import com.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.newbiechen.nbreader.ui.component.book.text.entity.TextChapter
import com.newbiechen.nbreader.ui.component.book.text.processor.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PageProgress
import com.newbiechen.nbreader.ui.component.book.text.processor.TextModel
import com.newbiechen.nbreader.ui.component.book.type.BookType
import com.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-11-03 20:55
 *  description :页面控制器
 */

class PageController(
    context: Context,
    textController: TextController
) {
    private var mContext = context
    private var mTextController = textController
    private var mBookPluginFactory = BookPluginFactory.getInstance(mContext)

    // 参数
    private var mCachePath: String? = null
    private var mInitChapterTitle: String? = null
    private var mChapterPattern: String? = null

    // 是否参数配置
    private var isConfigure = false

    // 文本内容解析器
    private var mFormatPlugin: NativeFormatPlugin? = null

    companion object {
        private const val TAG = "PageController"
    }

    /**********************************************功能方法**********************************************/

    /**
     * 设置配置项，必须在 open() 之前设置
     */
    fun setConfigure(
        cachePath: String,
        initChapterTitle: String,
        chapterPattern: String
    ) {
        mCachePath = cachePath
        mInitChapterTitle = initChapterTitle
        mChapterPattern = chapterPattern

        isConfigure = true
    }

    /**
     * 设置页面样式
     */
    fun setPageStyle() {
        // TODO:页面样式配置项，之后设置
    }

    /**
     * 设置页面回调
     */
    fun setPageListener(pageListener: OnPageListener) {
        mTextController.setPageListener(pageListener)
    }

    fun setPageActionListener(actionCallback: DefaultActionCallback) {
        mTextController.setPageActionListener(actionCallback)
    }

    /**
     * 打开本地书籍
     * @param bookPath：只支持本地书籍
     */
    fun open(bookPath: String, bookType: BookType) {
        check(isConfigure) {
            "please setConfigure() before open book"
        }
        LogHelper.i(TAG, "open: $bookType")

        // 根据类型获取插件
        mFormatPlugin = mBookPluginFactory.getPlugin(bookType)
        // 设置参数
        mFormatPlugin!!.setConfigure(mCachePath!!, mChapterPattern!!, mInitChapterTitle!!)
        // 打开书籍
        mFormatPlugin!!.openBook(bookPath)

        // TODO:TextModel 需要重构，下次再说
        // 创建本地文本模块
        var textModel = TextModel(mFormatPlugin!!)

        // 初始化页面内容控制器
        // TODO：需要传入的是 ChapterModel
        mTextController.initController(textModel)
        // TODO：如果 FormatPlugin 先改变，会导致 拿到的数据错误的问题，该怎么解决。
        // TODO：保证每次用的都是新创建的 TextModel，并且异步加载的时候，使用到 TextModel 的地方都需要做同步出离。
    }

    /**
     * 打开自定义书籍
     */
    fun open(bookGroup: BookGroup, bookType: BookType) {
        // 暂未实现
    }

    /**
     * 关闭书籍
     */
    fun close() {
        // 释放解析插件
        mFormatPlugin?.release()
        mFormatPlugin = null
    }

    /**
     * 跳转页面
     */
    fun skipPage(type: PageType) {
        mTextController.skipPage(type)
    }

    /**
     * 跳转章节
     */
    fun skipChapter(type: PageType) {
        mTextController.skipChapter(type)
    }

    /**
     * 跳转章节
     */
    fun skipChapter(index: Int) {
        mTextController.skipChapter(index)
    }

    /**********************************************返回信息方法**********************************************/

    /**
     * 是否书籍已经打开
     */
    fun isOpen(): Boolean {
        return mFormatPlugin != null
    }

    /**
     * 是否是支持的书籍类型
     */
    fun isSupportBookType(type: BookType): Boolean {
        return mBookPluginFactory.isSupportType(type)
    }

    /**
     * 获取支持的书籍类型
     */
    fun getSupportBookType(): List<BookType> {
        return mBookPluginFactory.getSupportPluginTypes()
    }

    /**
     * 获取书籍章节列表
     */
    fun getChapters(): Array<TextChapter> {
        // 检测是否书籍已经打开
        check(mFormatPlugin != null) {
            "please invoke open() before getChapters()"
        }

        // TODO:未处理章节不存在的情况
        return mFormatPlugin!!.getChapters()!!
    }

    /**
     * 获取当前的定位
     */
    fun getCurPosition(): PagePosition? {
        return mTextController.getPagePosition(PageType.CURRENT)
    }

    fun getCurProgress(): PageProgress? {
        return mTextController.getPageProgress(PageType.CURRENT)
    }

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType): Boolean {
        // 从内容控制器中判断，是否存在页面
        return mTextController.hasPage(type)
    }

    /**
     * 是否章节存在
     */
    fun hasChapter(type: PageType): Boolean {
        return mTextController.hasChapter(type)
    }
}