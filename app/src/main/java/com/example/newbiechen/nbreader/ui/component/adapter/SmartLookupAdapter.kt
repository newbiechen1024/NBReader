package com.example.newbiechen.nbreader.ui.component.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.LocalBookEntity
import com.example.newbiechen.nbreader.ui.component.book.type.BookType
import com.example.newbiechen.nbreader.databinding.ItemSmartLookupContentBinding
import com.example.newbiechen.nbreader.databinding.ItemSmartLookupHeadBinding
import com.example.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.example.newbiechen.nbreader.ui.component.adapter.base.PinnedHeaderAdapter
import com.example.newbiechen.nbreader.uilts.DateUtil
import com.example.newbiechen.nbreader.uilts.LogHelper
import com.example.newbiechen.nbreader.uilts.StringUtil

/**
 *  author : newbiechen
 *  date : 2019-08-20 15:00
 *  description :
 */

typealias OnCheckChangeCallback = (position: Int, isChecked: Boolean) -> Unit

class SmartLookupAdapter : PinnedHeaderAdapter<String, LocalBookWrapper>() {

    companion object {
        private const val TAG = "SmartLookupAdapter"
    }

    private var mCheckChangeCallback: OnCheckChangeCallback? = null

    /**
     * 选中书籍的数量
     */
    private var mCheckedCount: Int = 0

    override fun createHeaderViewHolder(): IViewHolder<String> = HeaderViewHolder()

    override fun createContentViewHolder(): IViewHolder<LocalBookWrapper> = ContentViewHolder()

    fun setCheckedAll(isChecked: Boolean) {
        // 重置选中数
        mCheckedCount = 0

        // 获取当前总组数
        mGroupList.flatMap {
            it.second  // 将 group 下的 List 合
        }.forEach {
            // 如果书籍未缓存
            if (!it.isCached) {
                // 设置选中态
                it.isChecked = isChecked

                if (isChecked) {
                    ++mCheckedCount
                }
            }
        }

        // 通知刷新
        notifyDataSetChanged()
    }

    fun getCheckedCount() = mCheckedCount

    fun getCheckedBooks(): List<LocalBookEntity> {
        var bookEntities = mutableListOf<LocalBookEntity>()

        mGroupList.flatMap {
            it.second  // 将 group 下的 List 合
        }.forEach {
            // 如果是选中的书籍，就添加
            if (it.isChecked) {
                bookEntities.add(it.localBookEntity)
            }
        }

        return bookEntities
    }

    fun setOnCheckedChangeListener(callback: OnCheckChangeCallback) {
        mCheckChangeCallback = callback
    }

    /**
     * 存储选中的书籍
     */
    fun saveCheckedBooks() {
        // 将选中的书籍设置为存储状态，并取消选中
        mGroupList.flatMap {
            it.second  // 将 group 下的 List 合
        }.forEach {

            if (it.isChecked) {
                it.isCached = true
                it.isChecked = false
                // 删除选中书籍的数量
                --mCheckedCount
            }
        }

        notifyDataSetChanged()
    }

    inner class HeaderViewHolder : IViewHolder<String> {
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

    inner class ContentViewHolder : IViewHolder<LocalBookWrapper> {
        private lateinit var mDataBinding: ItemSmartLookupContentBinding
        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            mDataBinding = ItemSmartLookupContentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return mDataBinding
        }

        override fun onBind(value: LocalBookWrapper, pos: Int) {
            val localBook = value.localBookEntity
            val colorId = when (localBook.type) {
                BookType.EPUB -> R.color.type_epub
                else -> R.color.type_txt
            }

            mDataBinding.apply {
                val typeBackground =
                    root.resources.getDrawable(R.drawable.bg_book_type) as GradientDrawable
                typeBackground.setColor(root.resources.getColor(colorId))
                tvName.text = localBook.name
                tvType.background = typeBackground
                tvType.text = localBook.type.name
                tvSize.text = StringUtil.size2Str(localBook.size)
                tvDate.text =
                    DateUtil.dateConvert(localBook.lastModified, DateUtil.FORMAT_FILE_DATE)
                // 是否可用
                cbAddBook.isEnabled = !value.isCached
                // 是否选中
                cbAddBook.isChecked = value.isChecked

                // 设置点击事件
                root.setOnClickListener {
                    // 对于已缓存的对象不支持点击
                    if (value.isCached) {
                        return@setOnClickListener
                    }

                    if (cbAddBook.isChecked) {
                        cbAddBook.isChecked = false
                        --mCheckedCount
                    } else {
                        cbAddBook.isChecked = true
                        ++mCheckedCount
                    }

                    value.isChecked = cbAddBook.isChecked
                    // 通知回调
                    mCheckChangeCallback?.invoke(pos, cbAddBook.isChecked)
                }
                executePendingBindings()
            }
        }
    }
}

data class LocalBookWrapper(
    val localBookEntity: LocalBookEntity, // 本地书籍元素
    var isCached: Boolean = false, // 是否缓存
    var isChecked: Boolean = false // 是否选中
)