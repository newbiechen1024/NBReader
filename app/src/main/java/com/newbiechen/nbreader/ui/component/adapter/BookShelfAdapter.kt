package com.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.newbiechen.nbreader.R
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.databinding.ItemBookShelfBinding
import com.newbiechen.nbreader.ui.component.adapter.base.IViewHolder
import com.newbiechen.nbreader.ui.component.adapter.base.SimpleBindingAdapter

/**
 *  author : newbiechen
 *  date : 2020-02-04 22:31
 *  description :书架列表
 */

class BookShelfAdapter : SimpleBindingAdapter<BookEntity>() {
    override fun createViewHolder(type: Int): IViewHolder<BookEntity> {
        return BookViewHolder()
    }

    inner class BookViewHolder : IViewHolder<BookEntity> {
        private lateinit var dataBinding: ItemBookShelfBinding

        override fun createBinding(parent: ViewGroup): ViewDataBinding {
            dataBinding = ItemBookShelfBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
            return dataBinding
        }

        override fun onBind(value: BookEntity, pos: Int) {
            // 做处理
            dataBinding.apply {
                val context = root.context
                title = value.title
                brief = context.getString(R.string.book_brief, "作者", "剩余章节数")
                detail = value.curChapter ?: "更新时间 : 当前阅读章节"
                isUpdate = value.isUpdate

                // 加载资源图片
                if (value.isLocal) {
                    //本地文件的图片
                    Glide.with(context)
                        .load(R.drawable.ic_local_book)
                        .into(ivBookIcon)
                } else {
                    // TODO:如果是网络书籍的情况
                }
            }
        }
    }
}