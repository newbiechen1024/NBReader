package com.example.newbiechen.nbreader.ui.component.adapter

import android.graphics.drawable.GradientDrawable
import android.util.LongSparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.databinding.ViewDataBinding
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.databinding.ItemSmartLookupContentBinding
import com.example.newbiechen.nbreader.databinding.ItemSmartLookupHeadBinding
import com.example.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.example.newbiechen.nbreader.ui.component.adapter.base.PinnedHeaderAdapter
import com.example.newbiechen.nbreader.uilts.DateUtil
import com.example.newbiechen.nbreader.uilts.StringUtil
import com.example.newbiechen.nbreader.uilts.mediastore.LocalBookInfo

/**
 *  author : newbiechen
 *  date : 2019-08-20 15:00
 *  description :
 */

typealias OnCheckChangeCallback = (position: Int, isChecked: Boolean) -> Unit

class SmartLookupAdapter : PinnedHeaderAdapter<String, LocalBookInfo>() {

    companion object {
        private const val TAG = "SmartLookupAdapter"
    }

    private var mCheckedList = LongSparseArray<LocalBookInfo>()
    private var mCheckChangeCallback: OnCheckChangeCallback? = null

    override fun createHeaderViewHolder(): IViewHolder<String> = HeaderViewHolder()

    override fun createContentViewHolder(): IViewHolder<LocalBookInfo> = ContentViewHolder()

    fun setCheckedAll(isChecked: Boolean) {
        if (isChecked) {
            // 获取当前总组数
            mGroupList.flatMap {
                it.second
            }.forEach {
                mCheckedList.put(it.id, it)
            }
        } else {
            mCheckedList.clear()
        }

        // 通知刷新
        notifyDataSetChanged()
    }

    fun getCheckedCount() = mCheckedList.size()

    fun getCheckedBookInfo(): List<LocalBookInfo> {
        var bookInfos = mutableListOf<LocalBookInfo>()
        mCheckedList.forEach { key, value ->
            bookInfos.add(value)
        }

        return bookInfos
    }

    fun setOnCheckedChangeListener(callback: OnCheckChangeCallback) {
        mCheckChangeCallback = callback
    }

    class HeaderViewHolder : IViewHolder<String> {
        private lateinit var mDataBinding: ItemSmartLookupHeadBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            mDataBinding = ItemSmartLookupHeadBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return mDataBinding
        }

        override fun onBind(value: String, pos: Int) {
            mDataBinding.title = value
            mDataBinding.executePendingBindings()
        }
    }

    inner class ContentViewHolder : IViewHolder<LocalBookInfo> {
        private lateinit var mDataBinding: ItemSmartLookupContentBinding
        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            mDataBinding = ItemSmartLookupContentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return mDataBinding
        }

        override fun onBind(value: LocalBookInfo, pos: Int) {
            var colorId = when (value.type) {
                BookType.EPUB -> R.color.type_epub
                else -> R.color.type_txt
            }

            mDataBinding.apply {
                var typeBackground = root.resources.getDrawable(R.drawable.bg_book_type) as GradientDrawable
                typeBackground.setColor(root.resources.getColor(colorId))
                tvName.text = value.name
                tvType.background = typeBackground
                tvType.text = value.type.name
                tvSize.text = StringUtil.size2Str(value.size)
                tvDate.text = DateUtil.dateConvert(value.lastModified, DateUtil.FORMAT_FILE_DATE)

                // TODO：之后书架完成了，还需要处理，该文件是否已经添加到文本的情况。
                cbAddBook.isChecked = mCheckedList.contains(value.id)
                // 设置点击事件
                root.setOnClickListener {
                    if (cbAddBook.isChecked) {
                        cbAddBook.isChecked = false
                        mCheckedList.remove(value.id)
                    } else {
                        cbAddBook.isChecked = true
                        mCheckedList.put(value.id, value)
                    }
                    // 通知回调
                    mCheckChangeCallback?.invoke(pos, cbAddBook.isChecked)
                }
                executePendingBindings()
            }
        }
    }
}