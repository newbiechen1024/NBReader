package com.newbiechen.nbreader.ui.component.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *  author : newbiechen
 *  date : 2020-02-12 23:35
 *  description :万能分割线
 *
 *  使用：https://blog.csdn.net/Common_it/article/details/89374546
 */

/**
 * @param orientation 方向类型
 * @param dividerColor       分割线颜色
 * @param dividerWidth    分割线宽度
 */
class DividerDecoration constructor(
    orientation: Int = VERTICAL_DIV,
    dividerColor: Int = Color.parseColor("#808080"),
    dividerWidth: Int = 1
) :
    RecyclerView.ItemDecoration() {
    companion object {
        private val TAG = "ItemDecorationPowerful"
        //横向布局分割线
        const val HORIZONTAL_DIV = 0
        //纵向布局分割线
        const val VERTICAL_DIV = 1
        //表格布局分割线
        const val GRID_DIV = 2
    }

    private val mPaint: Paint = Paint()

    private var mOrientation = VERTICAL_DIV
    private var mDividerWidth = 1

    init {
        mPaint.apply {
            isAntiAlias = true
            color = dividerColor
            style = Paint.Style.FILL
        }

        mDividerWidth = dividerWidth

        setOrientation(orientation)
    }

    /**
     * 初始化分割线类型
     *
     * @param orientation 分割线类型
     */
    private fun setOrientation(orientation: Int) {
        require(!(mOrientation != HORIZONTAL_DIV && mOrientation != VERTICAL_DIV && mOrientation != GRID_DIV)) { "ItemDecorationPowerful：分割线类型设置异常" }
        this.mOrientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        when (mOrientation) {
            HORIZONTAL_DIV ->  //横向布局分割线
                drawHorizontal(c, parent)
            VERTICAL_DIV ->  //纵向布局分割线
                drawVertical(c, parent)
            GRID_DIV ->  //表格格局分割线
                drawGrid(c, parent)
            else ->  //纵向布局分割线
                drawVertical(c, parent)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemPosition = parent.getChildAdapterPosition(view!!)
        val adapter = parent.adapter
        if (adapter != null) {
            val childCount: Int = adapter.itemCount
            when (mOrientation) {
                HORIZONTAL_DIV -> {
                    /**
                     * 横向布局分割线
                     *
                     * 如果是第一个Item，则不需要分割线
                     */

                    if (itemPosition != 0) {
                        outRect[mDividerWidth, 0, 0] = 0
                    }
                }

                VERTICAL_DIV -> {
                    /**
                     * 纵向布局分割线
                     *
                     * 如果是第一个Item，则不需要分割线
                     */

                    if (itemPosition != 0) {
                        outRect[0, mDividerWidth, 0] = 0
                    }
                }

                GRID_DIV -> {
                    /**
                     * 表格格局分割线
                     *
                     * 1：当是第一个Item的时候，四周全部需要分割线
                     * 2：当是第一行Item的时候，需要额外添加顶部的分割线
                     * 3：当是第一列Item的时候，需要额外添加左侧的分割线
                     * 4：默认情况全部添加底部和右侧的分割线
                     */
                    val layoutManager = parent.layoutManager
                    if (layoutManager is GridLayoutManager) {
                        val mGridLayoutManager: GridLayoutManager = layoutManager
                        val mSpanCount: Int = mGridLayoutManager.spanCount
                        when {
                            itemPosition == 0 -> { //1
                                outRect[mDividerWidth, mDividerWidth, mDividerWidth] = mDividerWidth
                            }
                            itemPosition + 1 <= mSpanCount -> { //2
                                outRect[0, mDividerWidth, mDividerWidth] = mDividerWidth
                            }
                            (itemPosition + mSpanCount) % mSpanCount == 0 -> { //3
                                outRect[mDividerWidth, 0, mDividerWidth] = mDividerWidth
                            }
                            else -> { //4
                                outRect[0, 0, mDividerWidth] = mDividerWidth
                            }
                        }
                    }
                }
                else -> {
                    //纵向布局分割线
                    if (itemPosition != childCount - 1) {
                        outRect[0, 0, 0] = mDividerWidth
                    }
                }
            }
        }
    }

    /**
     * 绘制横向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    open fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            drawLeft(c, child, parent)
        }
    }

    /**
     * 绘制纵向列表分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            drawTop(c, child, parent)
        }
    }

    /**
     * 绘制表格类型分割线
     *
     * @param c      绘制容器
     * @param parent RecyclerView
     */
    private fun drawGrid(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val layoutManager = parent.layoutManager
            if (layoutManager is GridLayoutManager) {
                val mGridLayoutManager: GridLayoutManager = layoutManager
                val mSpanCount: Int = mGridLayoutManager.getSpanCount()
                if (i == 0) {
                    drawTop(c, child, parent)
                    drawLeft(c, child, parent)
                }
                if (i + 1 <= mSpanCount) {
                    drawTop(c, child, parent)
                }
                if ((i + mSpanCount) % mSpanCount == 0) {
                    drawLeft(c, child, parent)
                }
                drawRight(c, child, parent)
                drawBottom(c, child, parent)
            }
        }
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param child       对应ItemView
     * @param recyclerView RecyclerView
     */
    private fun drawLeft(
        c: Canvas,
        child: View,
        recyclerView: RecyclerView
    ) {
        val childLayoutParams =
            child.layoutParams as RecyclerView.LayoutParams
        val left = child.left - mDividerWidth - childLayoutParams.leftMargin
        val top = child.top - childLayoutParams.topMargin
        val right = child.left - childLayoutParams.leftMargin
        val bottom: Int
        bottom = if (isGridLayoutManager(recyclerView)) {
            child.bottom + childLayoutParams.bottomMargin + mDividerWidth
        } else {
            child.bottom + childLayoutParams.bottomMargin
        }
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }

    /**
     * 绘制顶部分割线
     *
     * @param c            绘制容器
     * @param child       对应ItemView
     * @param recyclerView RecyclerView
     */
    private fun drawTop(
        c: Canvas,
        child: View,
        recyclerView: RecyclerView
    ) {
        val childLayoutParams =
            child.layoutParams as RecyclerView.LayoutParams
        val left: Int
        val top = child.top - childLayoutParams.topMargin - mDividerWidth
        val right = child.right + childLayoutParams.rightMargin
        val bottom = child.top - childLayoutParams.topMargin
        left = if (isGridLayoutManager(recyclerView)) {
            child.left - childLayoutParams.leftMargin - mDividerWidth
        } else {
            child.left - childLayoutParams.leftMargin
        }
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }

    /**
     * 绘制右边分割线
     *
     * @param c            绘制容器
     * @param child       对应ItemView
     * @param recyclerView RecyclerView
     */
    private fun drawRight(
        c: Canvas,
        child: View,
        recyclerView: RecyclerView
    ) {
        val childLayoutParams =
            child.layoutParams as RecyclerView.LayoutParams
        val left = child.right + childLayoutParams.rightMargin
        val top: Int
        val right = left + mDividerWidth
        val bottom = child.bottom + childLayoutParams.bottomMargin
        top = if (isGridLayoutManager(recyclerView)) {
            child.top - childLayoutParams.topMargin - mDividerWidth
        } else {
            child.top - childLayoutParams.topMargin
        }
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }

    /**
     * 绘制底部分割线
     *
     * @param c            绘制容器
     * @param child       对应ItemView
     * @param recyclerView RecyclerView
     */
    private fun drawBottom(
        c: Canvas,
        child: View,
        recyclerView: RecyclerView
    ) {
        val childLayoutParams =
            child.layoutParams as RecyclerView.LayoutParams
        val left = child.left - childLayoutParams.leftMargin
        val top = child.bottom + childLayoutParams.bottomMargin
        val bottom = top + mDividerWidth
        val right: Int
        right = if (isGridLayoutManager(recyclerView)) {
            child.right + childLayoutParams.rightMargin + mDividerWidth
        } else {
            child.right + childLayoutParams.rightMargin
        }
        c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
    }

    /**
     * 判断RecyclerView所加载LayoutManager是否为GridLayoutManager
     *
     * @param recyclerView RecyclerView
     * @return 是GridLayoutManager返回true，否则返回false
     */
    private fun isGridLayoutManager(recyclerView: RecyclerView): Boolean {
        val layoutManager = recyclerView.layoutManager
        return layoutManager is GridLayoutManager
    }
}