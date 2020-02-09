package com.newbiechen.nbreader.ui.component.widget.page

import com.newbiechen.nbreader.ui.component.book.plugin.NativeFormatPlugin
import com.newbiechen.nbreader.ui.component.book.text.entity.TextChapter
import com.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import com.newbiechen.nbreader.ui.component.book.type.BookType
import java.io.File

/**
 *  author : newbiechen
 *  date : 2019-11-03 20:55
 *  description :页面控制器
 *  @param pageDisplayController：页面显示控制器
 *  @param pageContentController：页面内容控制器
 */
class PageController(
    private val pageDisplayController: PageView,
    private val pageContentController: TextProcessor
) {

    private var mContext = pageDisplayController.context

    // 参数
    private var mCachePath: String? = null
    private var mInitChapterTitle: String? = null
    private var mChapterPattern: String? = null

    // 文本内容解析器
    private var mFormatPlugin: NativeFormatPlugin? = null

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
    }

    /**
     * 设置页面样式
     */
    fun setPageStyle() {
        // TODO:页面样式配置项，之后设置
    }

    /**
     * 打开书籍
     * @param bookPath：只支持本地书籍
     */
    fun open(bookPath: String, bookType: BookType) {
        check(File(bookPath).exists()) {
            "book path not found"
        }

        // TODO：检测书籍是否是支持的类型，如果不支持，直接抛出异常。

        // 创建插件
        mFormatPlugin = NativeFormatPlugin(mContext, bookType)

        // TODO:检测是否存在配置项
        // 设置参数
        mFormatPlugin!!.setConfigure(mCachePath!!, mChapterPattern!!, mInitChapterTitle!!)

        // 打开书籍
        mFormatPlugin!!.openBook(bookPath)

        // TODO：是直接 open 呢，还是配置成功后，然后再自己 open ?
        // TODO：如果存在书籍缓存怎么办？

        // 设置页面内容
        // TODO：需要传入的是 ChapterModel
        pageContentController.initProcessor(mFormatPlugin!!)

        // TODO：如果 FormatPlugin 先改变，会导致 拿到的数据错误的问题，该怎么解决。
        // TODO：比如 TextProcessor 在异步加载缓存数据，然后发现数据错误了。(由于 FormatPlugin 的改变，这个需要好好思考)
    }

    /**
     * 对于分章的网络书籍，传入 BookEntity，生成一个 Book
     */
    fun open() {
        // TODO：网络书籍需要特殊的处理方案
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
     * 跳转到指定位置
     */
    fun skipPosition() {

    }

    /**
     * 跳转页面
     */
    fun skipPage(type: PageType) {

    }

    /**
     * 跳转章节
     */
    fun skipChapter(type: PageType) {

    }

    /**********************************************设置回调方法**********************************************/

    /**
     * 设置点击事件回调
     */
    fun setActionListener() {

    }

    /**
     * 设置页面回调
     */
    fun setPageListener() {

    }

    /**
     * 设置书籍回调
     */
    fun setLoadListener() {

    }

    /**********************************************返回信息方法**********************************************/

    /**
     * 是否书籍已经打开
     */
    fun isOpen(): Boolean {
        return mFormatPlugin != null
    }

    /**
     * 获取书籍章节列表
     */
    fun getChapters(): Array<TextChapter> {
        // 检测是否书籍已经打开
        check(mFormatPlugin != null) {

        }

        // TODO:只是测试，实际没这么简单
        return mFormatPlugin!!.getChapters()!!
    }

    /**
     * 获取当前章节
     */
    fun getCurChapter() {

    }

    /**
     * 获取当前页面数量
     */
    fun getCurPageCount() {

    }

    /**
     * 获取当前的定位
     */
    fun getCurPosition() {

    }

    /**
     * 获取支持的书籍类型
     */
    fun getSupportBookType() {
        // NativePlugin 本身自带这个逻辑
    }

    /**
     * 是否页面存在
     */
    fun hasPage(type: PageType) {

    }

    /**
     * 是否章节存在
     */
    fun hasChapter(type: PageType) {

    }
}