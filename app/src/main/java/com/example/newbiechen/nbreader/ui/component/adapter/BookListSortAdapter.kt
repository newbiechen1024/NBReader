package com.example.newbiechen.nbreader.ui.component.adapter

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.databinding.ItemBookListSortBinding
import com.example.newbiechen.nbreader.ui.page.base.adapter.SimpleBindingAdapter
import com.example.newbiechen.nbreader.ui.page.base.adapter.IViewHolder
import com.example.newbiechen.nbreader.ui.page.base.adapter.OnItemClickListener
import com.example.newbiechen.nbreader.uilts.DensityUtil

/**
 *  author : newbiechen
 *  date : 2019-08-07 16:07
 *  description :书籍排序列表
 */

class BookListSortAdapter : SimpleBindingAdapter<String>() {

    private var mItemClickListener: OnItemClickListener<String>? = null
    private var mSelectedPos: Int = 0

    override fun createViewHolder(type: Int): IViewHolder<String> {
        return BookFeatureViewHolder()
    }

    inner class BookFeatureViewHolder : IViewHolder<String> {
        private lateinit var dataBinding: ItemBookListSortBinding
        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            dataBinding = ItemBookListSortBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return dataBinding
        }

        override fun onBind(value: String, pos: Int) {

            dataBinding.tvSort.text = value
            dataBinding.root.setOnClickListener {
                mItemClickListener?.invoke(pos, value)

                mSelectedPos = pos
                // 通知刷新
                notifyDataSetChanged()
            }

            dataBinding.tvSort.setTextColor(
                dataBinding.root.context.resources
                    .getColor(if (mSelectedPos == pos) R.color.text_emphasized else R.color.text_book_list_sort)
            )

            dataBinding.executePendingBindings()
        }
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener<String>) {
        mItemClickListener = itemClickListener
    }

    class BookListSortDecoration : RecyclerView.ItemDecoration() {
        private val mPaint: Paint = Paint().apply {
            strokeCap = Paint.Cap.ROUND
            style = Paint.Style.STROKE
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            // 设置 paint 的颜色
            mPaint.color = parent.context.resources.getColor(R.color.bg_divider)
            mPaint.strokeWidth = DensityUtil.dp2px(parent.context, 1f).toFloat()

            // 获取 parent 的高度
            val parentHeight = parent.height
            // 分割线的高度
            val dividerHeight = parent.context.resources.getDimensionPixelSize(R.dimen.height_book_list_feature_divider)

            val disTop = parent.top + (parentHeight - dividerHeight) / 2
            var disLeft = parent.paddingLeft
            for (i in 0 until (parent.childCount - 1)) {
                // 获取子 View ，测量子 View 的宽度
                val viewWidth = parent.getChildAt(i).width
                disLeft += viewWidth
                // 进行绘制
                c.drawLine(
                    disLeft.toFloat(),
                    disTop.toFloat(),
                    disLeft.toFloat(),
                    (disTop + dividerHeight).toFloat(),
                    mPaint
                )
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            // 不处理最后一个元素
            if (parent.getChildAdapterPosition(view) < (parent.childCount - 1)) {
                // 设置一个像素
                outRect.right = 1
            }
        }
    }
}