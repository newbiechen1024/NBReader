package com.newbiechen.nbreader.ui.component.widget.page

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.FloatRange
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.ui.component.book.text.config.TextConfig
import com.newbiechen.nbreader.ui.component.book.text.entity.TextFixedPosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PagePosition
import com.newbiechen.nbreader.ui.component.book.text.processor.PageProgress
import com.newbiechen.nbreader.ui.component.book.text.processor.TextProcessor
import com.newbiechen.nbreader.ui.component.widget.page.PageManager.OnPageListener
import com.newbiechen.nbreader.ui.component.widget.page.action.*
import com.newbiechen.nbreader.ui.component.widget.page.anim.*
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageView
import com.newbiechen.nbreader.uilts.LogHelper
import java.lang.Exception
import kotlin.math.abs

/**
 *  author : newbiechen
 *  date : 2020-01-26 14:22
 *  description : 页面容器类
 */

typealias OnPreparePageListener = (pagePosition: PagePosition, pageProgress: PageProgress) -> Unit

class PageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), OnPageListener {

    companion object {
        private const val TAG = "PageView"
        private const val MAX_CHILD_VIEW = 3

        // 壁纸
        private var sWallpaperPath: String? = null
        private var sWallpaperBitmap: Bitmap? = null
    }

    // 页面管理器
    private var mPageManager: PageManager = PageManager(this)

    // 文本处理器
    private var mTextProcessor: TextProcessor = TextProcessor(context)

    // 页面控制器
    private var mPageController: PageController = PageController(this, mTextProcessor)

    // todo：文本配置信息(暂时先放这里，等弄到动态修改文本配置项时再处理，感觉 TextConfig 不应该直接从 TextProcessor 中获取)
    private var mTextConfig: TextConfig = mTextProcessor.getTextConfig()

    // 当前动画类型
    private var mPageAnimType: PageAnimType? = null

    // 当前翻页动画
    private var mPageAnim: PageAnimation? = null

    // 页面行为事件监听器
    private var mPageActionListener: TextActionListener? = null

    private var mPreparePageListener: OnPreparePageListener? = null

    // 唤起菜单区域(默认选中区域)
    private var mMenuRatioRect: RectF = RectF(0.2f, 0.3f, 0.8f, 0.7f)

    private var mMenuRect: RectF = RectF()

    private var mBackgroundRect: Rect = Rect()

    // 容器
    private lateinit var mFlHeader: FrameLayout
    private lateinit var mFlContent: FrameLayout
    private lateinit var mFlFooter: FrameLayout

    // 页面内容文本
    private lateinit var mPtvContent: TextPageView

    init {
        // 设置排列方式
        orientation = VERTICAL
        // 初始化子 View
        initView()
        // 设置默认颜色，触发 ViewGroup 的 onDraw()
        setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
    }

    private fun initView() {
        // 添加 Page 布局
        LayoutInflater.from(context).inflate(R.layout.layout_page, this, true)

        // 获取布局
        mFlHeader = findViewById(R.id.page_fl_header)
        mFlContent = findViewById(R.id.page_fl_content)
        mFlFooter = findViewById(R.id.page_fl_footer)

        // 初始化页面文本视图
        initPageTextView()

        // 设置默认动画
        setPageAnim(PageAnimType.SIMULATION)
    }

    private fun initPageTextView() {
        mPtvContent = TextPageView(context)
        mPtvContent.setTextProcessor(mTextProcessor)
        mPtvContent.setPageActionListener(this::onPageAction)

        val contentParams =
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)

        // 将页面内容展示添加到容器中
        mFlContent.addView(mPtvContent, contentParams)
    }

    /**
     * 添加顶部
     */
    fun setHeaderView(view: View, params: FrameLayout.LayoutParams? = null) {
        // 如果 header 已存在
        if (mFlHeader.childCount != 0) {
            mFlHeader.removeAllViews()
        }

        // 添加 view 到布局中
        if (params != null) {
            mFlHeader.addView(view, params)
        } else {
            mFlHeader.addView(view)
        }
        mFlHeader.visibility = View.VISIBLE
    }

    /**
     * 添加尾部
     */
    fun setFooterView(view: View, params: FrameLayout.LayoutParams? = null) {
        if (mFlFooter.childCount != 0) {
            mFlFooter.removeAllViews()
        }

        // 天啊及
        if (params != null) {
            mFlFooter.addView(view, params)
        } else {
            mFlFooter.addView(view)
        }

        mFlFooter.visibility = View.VISIBLE
    }

    /**
     * 设置事件监听
     */
    fun setActionListener(pageActionListener: TextActionListener) {
        mPageActionListener = pageActionListener
    }

    /**
     * 设置唤起菜单点击区域
     * @param widthRatio：[0,1]。与 PageView 宽的比例
     * @param heightRatio：[0,1]。与 PageView 高的比例
     */
    fun setMenuArea(
        @FloatRange(from = 0.0, to = 1.0) widthRatio: Float,
        @FloatRange(from = 0.0, to = 1.0) heightRatio: Float
    ) {
        val leftRatio = 0.5f - widthRatio / 2f
        val rightRatio = 0.5f + widthRatio / 2f
        val topRatio = 0.5f - heightRatio / 2f
        val bottomRatio = 0.5f - heightRatio / 2f

        // 获取菜单区域比例
        mMenuRatioRect.set(leftRatio, topRatio, rightRatio, bottomRatio)

        // 设置菜单选中区域
        mMenuRect.set(
            mMenuRatioRect.left * width,
            mMenuRatioRect.top * height,
            mMenuRatioRect.right * width,
            mMenuRatioRect.bottom * height
        )
    }

    /**
     * 设置页面动画类型
     */
    fun setPageAnim(type: PageAnimType) {
        if (mPageAnimType != type) {
            mPageAnim = when (type) {
                PageAnimType.NONE -> NonePageAnimation(this, mPageManager)
                PageAnimType.COVER -> CoverPageAnimation(this, mPageManager)
                PageAnimType.SLIDE -> SlidePageAnimation(this, mPageManager)
                PageAnimType.SIMULATION -> SimulationPageAnimation(this, mPageManager)
                PageAnimType.SCROLL -> null
            }

            if (mPageAnim != null) {
                mPageAnim!!.setAnimationListener(mPageManager)
                // 重置宽高
                mPageAnim!!.setup(width, height)
            }


            // 设置 TextPageView 的页面模式
            if (type == PageAnimType.SCROLL) {
                mPtvContent.setPageMode(TextPageView.PageMode.SCROLL)
            } else {
                mPtvContent.setPageMode(TextPageView.PageMode.NONE)
            }

            mPageAnimType = type
            invalidate()
        }
    }

    fun setOnPreparePageListener(preparePageListener: OnPreparePageListener) {
        mPreparePageListener = preparePageListener
    }

    /**
     * 获取页面控制器
     */
    fun getPageController() = mPageController

    /**
     * 进行翻页操作
     */
    internal fun skipPage(type: PageType) {
        // 通过模拟点击进行翻页操作
        when (type) {
            PageType.PREVIOUS -> {
                mCurDrawType = null
                releasePage(0, height / 2)
            }
            PageType.NEXT -> {
                mCurDrawType = null
                releasePage(width, height / 2)
            }
            else -> {
            }
        }
    }

    internal fun skipChapter(type: PageType) {
        mCurDrawType = null
        mPtvContent.skipChapter(type)
        postInvalidate()
    }

    /**
     * 跳转章节操作
     * @param index:章节索引
     */
    internal fun skipChapter(index: Int) {
        mCurDrawType = null
        // 如果索引存在
        mPtvContent.skipChapter(index)
        postInvalidate()
    }

    /**
     * 跳转页面操作
     */
    internal fun skipPage(chapterIndex: Int = mPtvContent.getCurChapterIndex(), pageIndex: Int) {
        mCurDrawType = null

        mPtvContent.skipPage(chapterIndex, pageIndex)
        postInvalidate()
    }

    /**
     * 进行跳转页面操作
     */
    internal fun skipPage(position: TextFixedPosition) {
        mCurDrawType = null

        // 调用页面内容 View 进行页面跳转
        mPtvContent.skipPage(position)
        postInvalidate()
    }

    /**
     * 监听 PageTextView 返回的点击事件
     */
    private fun onPageAction(action: PageAction) {
        when (action) {
            is MotionAction -> {
                onPageMotionEvent(action)
            }
            else -> {
                mPageActionListener?.invoke(action)
            }
        }
    }

    /**
     * 页面移动事件处理
     */
    private fun onPageMotionEvent(action: MotionAction) {
        // 消耗发出的行为事件
        when (action.type) {
            MotionType.PRESS -> {
                action.apply {
                    mPageAnim?.pressPage(event.x.toInt(), event.y.toInt())
                }
            }
            MotionType.MOVE -> {
                action.apply {
                    mPageAnim?.movePage(event.x.toInt(), event.y.toInt())
                }
            }
            MotionType.RELEASE -> {
                action.apply {
                    releasePage(event.x.toInt(), event.y.toInt())
                }
            }
            MotionType.SINGLE_TAP -> {
                action.apply {
                    // 如果点击区域在菜单范围内，则发送点击菜单行为事件
                    if (mMenuRect.contains(event.x, event.y)) {
                        mPageActionListener?.invoke(TapMenuAction())
                    }
                }
            }
        }
    }

    private fun releasePage(x: Int, y: Int) {
        mPageAnim?.releasePage(x, y)
    }

    override fun addView(child: View?) {
        check(childCount <= MAX_CHILD_VIEW) { "Unsupported add extra view" }
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        check(childCount <= MAX_CHILD_VIEW) { "Unsupported add extra view" }
        super.addView(child, index)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        check(childCount <= MAX_CHILD_VIEW) { "Unsupported add extra view" }
        super.addView(child, width, height)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        check(childCount <= MAX_CHILD_VIEW) { "Unsupported add extra view" }
        super.addView(child, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        check(childCount <= MAX_CHILD_VIEW) { "Unsupported add extra view" }
        super.addView(child, index, params)
    }

    override fun setOrientation(orientation: Int) {
        if (orientation != VERTICAL) {
            throw IllegalAccessException("unsupported other orientation except vertical")
        }
        super.setOrientation(orientation)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 设置页面动画大小
        mPageAnim?.setup(w, h)

        // 设置菜单选中区域
        mMenuRect.set(
            mMenuRatioRect.left * width,
            mMenuRatioRect.top * height,
            mMenuRatioRect.right * width,
            mMenuRatioRect.bottom * height
        )
        mBackgroundRect.set(0, 0, w, h)
    }

    private var mPressedX = 0
    private var mPressedY = 0

    private var isMove = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x.toInt()
        val y = event.y.toInt()

        // 点击事件处理
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onPageMotionEvent(MotionAction(MotionType.PRESS, event))
                mPressedX = x
                mPressedY = y
                isMove = false
            }
            MotionEvent.ACTION_MOVE -> {
                // 最小滑动距离
                val minSlop = ViewConfiguration.get(context).scaledTouchSlop

                if (!isMove) {
                    isMove = abs(mPressedX - x) > minSlop || abs(mPressedY - y) > mPressedY
                }

                // 如果移动，直接取消双击事件
                if (isMove) {
                    onPageMotionEvent(MotionAction(MotionType.MOVE, event))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isMove) {
                    onPageMotionEvent(MotionAction(MotionType.SINGLE_TAP, event))
                } else {
                    onPageMotionEvent(MotionAction(MotionType.RELEASE, event))
                }
            }
        }

        return true
    }

    override fun dispatchDraw(canvas: Canvas?) {
        if (mPageAnim == null) {
            super.dispatchDraw(canvas)
        } else {
            // 直接绘制动画，不进行分发操作。
            mPageAnim!!.draw(canvas!!)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // TODO:这个操作感觉怪怪的，有没有更好的方案
        if (mPageAnim == null) {
            LogHelper.i(TAG, "onDraw: mPageAnim")
            // 直接绘制背景
            drawBackground(canvas!!)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 如果页面退出，则取消动画
        mPageAnim?.abortAnim()
    }

    override fun computeScroll() {
        // 处理翻页动画，滑动事件
        mPageAnim?.computeScroll()
    }

    override fun hasPage(type: PageType): Boolean {
        return mPtvContent.hasPage(type)
    }

    private var mCurDrawType: PageType? = null

    override fun drawPage(canvas: Canvas, type: PageType) {

        // TODO:这个可以放在 onDraw 中处理吧？
        // 绘制背景信息
        drawBackground(canvas)

        // 准备要绘制的页面
        mPtvContent.preparePage(type)

        // 如果绘制新页面，则通知页面准备
        if (mCurDrawType != type) {
            val pagePosition = mPtvContent.getPagePosition(type)
            val pageProgress = mPtvContent.getPageProgress(type)

            if (pagePosition != null && pageProgress != null) {
                mPreparePageListener?.invoke(pagePosition, pageProgress)
            }

            mCurDrawType = type
        }

        // 调用父类分发绘制逻辑
        super.dispatchDraw(canvas)
    }

    /**
     * 绘制背景页面
     */
    private fun drawBackground(canvas: Canvas) {
/*        if (!TextUtils.isEmpty(mTextConfig.wallpaperPath)) {
            drawWallpaper(canvas, mTextConfig.wallpaperPath)
        } else {
            canvas.drawColor(mTextConfig.bgColor)
        }*/

        // TODO:测试壁纸
        drawWallpaper(canvas, "wallpaper/paper.jpg")
    }

    /**
     * 绘制背景
     * TODO:暂时默认认为 wallpaper 都是从 asset 中获取的
     */
    private fun drawWallpaper(canvas: Canvas, wallpaperPath: String) {
        if (wallpaperPath != sWallpaperPath) {
            try {
                val fileInputStream = context.assets.open(wallpaperPath)
                // 获取图片资源
                sWallpaperBitmap = BitmapFactory.decodeStream(fileInputStream)
            } catch (e: Exception) {
                LogHelper.e(TAG, e.toString())
            }
        }

        if (sWallpaperBitmap != null) {
            // 直接绘制图片
            canvas.drawBitmap(sWallpaperBitmap!!, null, mBackgroundRect, null)
        }
    }

    override fun onTurnPage(pageType: PageType) {
        // 请求文本翻页
        mPtvContent.turnPage(pageType)
    }
}