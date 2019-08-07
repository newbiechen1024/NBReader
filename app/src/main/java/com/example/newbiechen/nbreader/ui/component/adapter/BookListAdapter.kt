package com.example.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newbiechen.nbreader.R
import com.example.newbiechen.nbreader.data.entity.BookEntity
import com.example.newbiechen.nbreader.databinding.ItemBookListBinding
import com.example.newbiechen.nbreader.uilts.Constants
import com.example.newbiechen.nbreader.uilts.StringUtil

/**
 *  author : newbiechen
 *  date : 2019-08-05 18:30
 *  description :
 */

class BookListAdapter : PagedListAdapter<BookEntity, BookListAdapter.BookListViewHolder>(diffCallback) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<BookEntity>() {
            override fun areItemsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: BookEntity, newItem: BookEntity): Boolean {
                // BookList 不做刷新处理，所以只需要判断 id 就行了，内容没有必要判断。
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
        val dataBinding = ItemBookListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookListViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
        holder.bind(getItem(position)!!, position)
    }

    inner class BookListViewHolder(private val dataBinding: ItemBookListBinding) :
        RecyclerView.ViewHolder(dataBinding.root) {

        fun bind(bookEntity: BookEntity, pos: Int) {
            dataBinding.coverUrl = Constants.IMG_BASE_URL + bookEntity.cover
            dataBinding.title = bookEntity.title
            dataBinding.brief = itemView.context.getString(R.string.book_brief, bookEntity.author, bookEntity.minorCate)
            dataBinding.hot = itemView.context.getString(
                R.string.book_hot,
                StringUtil.number2Str(itemView.context, bookEntity.latelyFollower)
            )
        }
    }
}