package com.example.newbiechen.nbreader.ui.component.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter

/**
 *  author : newbiechen
 *  date : 2019-08-11 15:48
 *  description :
 */

class SpaceItemDecoration(private val horizonSpace: Int? = null, private val verticalSpace: Int? = null) :
    RecyclerView.ItemDecoration() {

    // 设置第一行的顶部间距
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val layoutManager = parent.layoutManager!!

        var headLastPosition = 0
        var footStartPosition = parent.adapter!!.itemCount

        // 处理使用 LRecyclerView 框架的情况
        val adapter = parent.adapter

        if (adapter is LRecyclerViewAdapter) {
            headLastPosition = adapter.headerViewsCount + 1 // + 1 是因为 LRecyclerView 默认有一个 header
            footStartPosition -= adapter.footerViewsCount
        }

        if (layoutManager is GridLayoutManager) {
            var position = parent.getChildAdapterPosition(view)

            if (position >= headLastPosition) {

                if (verticalSpace != null) {
                    // 设置顶部 space
                    if ((position - headLastPosition) < layoutManager.spanCount) {
                        outRect.top = verticalSpace
                    }

                    if (position < footStartPosition) {
                        outRect.bottom = verticalSpace
                    }
                }

                // 解决 divider 导致 item 大小不同的问题
                if (horizonSpace != null && position < footStartPosition) {
                    // 计算这个child 处于第几列
                    val column = (position - headLastPosition) % layoutManager.spanCount
                    outRect.left = (column * horizonSpace / layoutManager.spanCount)
                    outRect.right = horizonSpace - (column + 1) * horizonSpace / layoutManager.spanCount
                }
            }
        } else if (layoutManager is LinearLayoutManager) {
            if (layoutManager.orientation == LinearLayoutManager.HORIZONTAL) {
                // 如果没有横向值，则不处理
                if (horizonSpace == null) {
                    return
                }

                // 不处理 HORIZONTAL 情况下的 LRecyclerView

                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.left = horizonSpace
                }
                outRect.right = horizonSpace
            } else {
                // 如果没有纵向值，则不处理
                if (verticalSpace == null) {
                    return
                }

                // 对于 Header 和 Footer 不设置 space
                val position = parent.getChildAdapterPosition(view)

                if (position >= headLastPosition) {
                    if (position == headLastPosition) {
                        outRect.top = verticalSpace
                    }

                    if (position < footStartPosition) {
                        outRect.bottom = verticalSpace
                    }
                }
            }
        }
    }
}