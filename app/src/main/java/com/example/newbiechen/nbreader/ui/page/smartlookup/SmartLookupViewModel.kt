package com.example.newbiechen.nbreader.ui.page.smartlookup

import android.text.TextUtils
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.newbiechen.nbreader.uilts.mediastore.BookInfo
import com.example.newbiechen.nbreader.uilts.mediastore.MediaStoreHelper
import com.github.promeg.pinyinhelper.Pinyin

/**
 *  author : newbiechen
 *  date : 2019-08-19 16:22
 *  description :
 */

class SmartLookupViewModel : ViewModel() {
    val isLoading = ObservableField(true)
    val bookInfoGroups = ObservableField<List<Pair<String, List<BookInfo>>>>()
    /**
     * 加载本地书籍
     */
    fun loadLocalBookInfo(activity: FragmentActivity) {
        // TODO:需要传入分类类型,如：根据拼音分类，根据日期分类
        // 现在暂时根据拼音分类
        MediaStoreHelper.cursorLocalBooks(activity) {
            bookInfoGroups.set(createGroupByLetter(it))
            isLoading.set(false)
        }
    }

    fun deleteBookInfos(bookInfoIds: List<Long>) {
        if (bookInfoIds.isEmpty()) {
            return
        }

        var groups = bookInfoGroups.get()
        if (groups != null) {
            for (i in 0 until groups.size) {
                var bookInfoIt = (groups[i].second as MutableList<BookInfo>).iterator()
                // 遍历 bookInfo
                while (bookInfoIt.hasNext()) {
                    var bookInfo = bookInfoIt.next()
                    // 如果包含该 id 则删除
                    if (bookInfoIds.contains(bookInfo.id)) {
                        bookInfoIt.remove()
                    }
                }
            }

            bookInfoGroups.set(groups)
        }
    }

    private fun createGroupByLetter(bookInfos: List<BookInfo>): List<Pair<String, List<BookInfo>>> {
        // TODO：是否应该异步处理？？

        var curGroupType: String? = null
        var groupList: MutableList<Pair<String, List<BookInfo>>> = mutableListOf()
        var groupItem: Pair<String, List<BookInfo>>? = null
        var itemList: MutableList<BookInfo>? = null
        bookInfos.forEach {
            var type = Pinyin.toPinyin(it.name[0])
            // 如果 type 为空则不处理
            if (type.isEmpty()) {
                return@forEach
            }
            // 获取拼音的第一个字符，并设置为大写
            type = type[0].toString().toUpperCase()

            if (curGroupType == null || !TextUtils.equals(curGroupType, type)) {
                curGroupType = type
                // 创建 list
                itemList = mutableListOf()
                itemList!!.add(it)

                // 创建 groupItem
                groupItem = Pair(type, itemList!!)
                // 添加到列表中
                groupList.add(groupItem!!)
            } else {
                itemList!!.add(it)
            }
        }

        return groupList
    }

    private fun createGroupByDate() {

    }
}