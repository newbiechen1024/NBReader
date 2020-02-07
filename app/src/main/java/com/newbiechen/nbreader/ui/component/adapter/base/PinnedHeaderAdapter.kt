package com.newbiechen.nbreader.ui.component.adapter.base

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 *  author : newbiechen
 *  date : 2019-08-19 17:05
 *  description : 分组适配器
 *
 *  注：仅支持 LinearLayout
 *
 *  任务：
 *
 *  1. 支持隐藏 first header
 *  2. Header 的排序问题怎么解决
 */

abstract class PinnedHeaderAdapter<T, R> : RecyclerView.Adapter<WrapViewHolder<*>>() {
    companion object {
        private const val TYPE_HEADER = 0x1001
        private const val TYPE_CONTENT = 0x1002
    }

    protected var mGroupList: MutableList<Pair<T, List<R>>> = mutableListOf()
    // 缓存 Group 的位置信息
    private var mGroupPosCache: SparseArray<Pair<Int, Int>> = SparseArray()

    abstract fun createHeaderViewHolder(): IViewHolder<T>
    abstract fun createContentViewHolder(): IViewHolder<R>

    open fun bindViewHolder(binding: ViewDataBinding, position: Int) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WrapViewHolder<*> {
        // 创建 holder
        val holder = when (viewType) {
            TYPE_HEADER -> createHeaderViewHolder()
            else -> createContentViewHolder()
        }
        // 创建 binding
        val binding = holder.createBinding(parent)
        return WrapViewHolder(binding, holder)
    }

    override fun getItemCount(): Int {
        // header 的数量
        var itemCount = mGroupList.size
        // content 的数量
        mGroupList.forEach {
            itemCount += it.second.size
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) {
            TYPE_HEADER
        } else {
            TYPE_CONTENT
        }
    }

    fun isHeader(position: Int): Boolean {
        return getGroupPosition(position).second == 0
    }

    fun isContent(position: Int): Boolean {
        return getGroupPosition(position).second != 0
    }

    /**
     * group 从 0 开始
     * content: 0 表示 header，1 表示数据内容
     */
    fun getGroupPosition(position: Int): Pair<Int, Int> {
        if (mGroupPosCache.containsKey(position)) {
            return mGroupPosCache[position]
        } else {
            // 从 1 开始计数
            var curPosition = position
            // -1 表示数据不存在
            var groupIndex = -1
            for (i in 0 until mGroupList.size) {
                // 当前分组 + 1
                groupIndex += 1
                var contentCount = mGroupList[i].second.size
                if (curPosition <= contentCount) {
                    break
                } else {
                    curPosition -= (contentCount + 1)// 还需要减去一个 header
                }
            }
            return Pair(groupIndex, curPosition).also {
                // 添加到 group 缓存中
                mGroupPosCache.put(position, it)
            }
        }
    }

    /**
     * 添加组
     */
    fun addGroup(key: T, value: List<R>) {
        mGroupList.add(Pair(key, value))
        notifyDataSetChanged()
    }

    fun sortGroupList(comparator: Comparator<Pair<T, List<R>>>) {
        mGroupList.sortWith(comparator)
    }

    /**
     * 刷新组
     */
    fun refreshAllGroup(groupList: List<Pair<T, List<R>>>) {
        // 替换
        mGroupList.clear()
        mGroupPosCache.clear()

        mGroupList.addAll(groupList)
        notifyDataSetChanged()
    }

    // 获取组数
    fun getGroupCount(): Int {
        return mGroupList.size
    }

    fun getGroupChildCount(): Int {
        return mGroupList.flatMap { it.second }.size
    }

    fun getGroupContent(groupIndex: Int): List<R> {
        return mGroupList[groupIndex].second
    }

    private fun getItem(position: Int): Any {
        var groupPosition = getGroupPosition(position)
        // 判断是否是 header
        return if (groupPosition.second == 0) {
            mGroupList[groupPosition.first].first as Any
        } else {
            mGroupList[groupPosition.first].second[groupPosition.second - 1] as Any
        }
    }

    override fun onBindViewHolder(holder: WrapViewHolder<*>, position: Int) {
        if (isHeader(position)) {
            val realHolder = holder.holder as IViewHolder<T>
            realHolder.onBind(getItem(position) as T, position)
        } else {
            val realHolder = holder.holder as IViewHolder<R>
            realHolder.onBind(getItem(position) as R, position)
        }
    }
}