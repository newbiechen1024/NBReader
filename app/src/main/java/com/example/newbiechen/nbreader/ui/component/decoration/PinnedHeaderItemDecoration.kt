package com.example.newbiechen.nbreader.ui.component.decoration

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.ui.component.adapter.base.PinnedHeaderAdapter
import com.example.newbiechen.nbreader.ui.component.adapter.base.WrapViewHolder
import android.R.layout
import android.graphics.Color
import androidx.recyclerview.widget.DividerItemDecoration
import android.opengl.ETC1.getHeight
import androidx.core.view.marginLeft
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.uilts.LogHelper

/**
 *  author : newbiechen
 *  date : 2019-08-20 18:22
 *  description :
 */

class PinnedHeaderItemDecoration : RecyclerView.ItemDecoration() {
    companion object {
        private const val TAG = "PinnedHeaderItemDecoration"
        private const val INVALID_POS = -1
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var pinnedAdapter = parent.adapter
        if (pinnedAdapter is PinnedHeaderAdapter<*, *> && parent.childCount > 0) {
            // 获取当前 RecyclerView 第一个展示的 view 在 adapter 中的 position
            var firstViewAdapterPos = parent.getChildAdapterPosition(parent.getChildAt(0))
            // 根据 position 向上查找其 header
            var pinnedHeaderAdapterPos = findPinnedHeaderViewPosition(firstViewAdapterPos, pinnedAdapter)
            if (pinnedHeaderAdapterPos != INVALID_POS) {
                // 创建 header view
                var viewHolder = pinnedAdapter.createHeaderViewHolder()
                var wrapViewHolder: WrapViewHolder<*> = WrapViewHolder(viewHolder.createBinding(parent), viewHolder)
                pinnedAdapter.bindViewHolder(wrapViewHolder, pinnedHeaderAdapterPos)
                val pinnedView = wrapViewHolder.itemView
                // 计算 pinnedView 的 layout
                layoutPinnedHeader(pinnedView, parent)
                var sectionPinOffset = 0
                // 遍历当前 RV 显示的 child
                for (index in 0 until parent.childCount) {
                    // 查找 child 中为 header 的 view
                    if (pinnedAdapter.isHeader(parent.getChildAdapterPosition(parent.getChildAt(index)))) {
                        val sectionView = parent.getChildAt(index)
                        val sectionTop = sectionView.top
                        val pinViewHeight = pinnedView.height
                        // 如果当前 pinned 的位置已经在其大小范围内，则确定偏移
                        if (sectionTop in 1 until pinViewHeight) {
                            sectionPinOffset = sectionTop - pinViewHeight
                        }
                    }
                }

                c.save()
                val pinnedLayoutParams = pinnedView.layoutParams as RecyclerView.LayoutParams
                // 偏移绘制区域
                c.translate(pinnedLayoutParams.leftMargin.toFloat() + parent.paddingLeft, sectionPinOffset.toFloat())
                // 设置绘制区域
                c.clipRect(0, 0, parent.width - parent.paddingLeft - parent.paddingRight, pinnedView.measuredHeight)
                // 将 header 的内容绘制到绘制区域上
                pinnedView.draw(c)
                c.restore()
            }
        }
    }

    private fun findPinnedHeaderViewPosition(index: Int, adapter: PinnedHeaderAdapter<*, *>): Int {
        for (i in index downTo 0) {
            if (adapter.isHeader(i)) {
                return i
            }
        }
        return -1
    }

    private fun layoutPinnedHeader(pinnedView: View, parent: RecyclerView) {
        if (pinnedView.isLayoutRequested) {
            val layoutParams = pinnedView.layoutParams as RecyclerView.LayoutParams
            val parentWidth = parent.width - parent.paddingLeft - parent.paddingRight
            // 计算 pinnedView 的宽
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                parentWidth - layoutParams.leftMargin - layoutParams.rightMargin,
                View.MeasureSpec.EXACTLY
            )
            // 计算 pinnedView 的高
            val heightSpec: Int
            heightSpec = if (layoutParams.height > 0) {
                View.MeasureSpec.makeMeasureSpec(layoutParams.height, View.MeasureSpec.EXACTLY)
            } else {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            }
            pinnedView.measure(widthSpec, heightSpec)
            pinnedView.layout(0, 0, pinnedView.measuredWidth, pinnedView.measuredHeight)
        }
    }
}