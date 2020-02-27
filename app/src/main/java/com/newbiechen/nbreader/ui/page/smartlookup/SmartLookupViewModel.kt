package com.newbiechen.nbreader.ui.page.smartlookup

import android.text.TextUtils
import android.util.LongSparseArray
import android.util.Range
import androidx.core.util.contains
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import com.newbiechen.nbreader.data.entity.BookEntity
import com.newbiechen.nbreader.data.entity.LocalBookEntity
import com.newbiechen.nbreader.data.repository.BookRepository
import com.newbiechen.nbreader.ui.component.adapter.LocalBookWrapper
import com.newbiechen.nbreader.ui.page.base.BaseViewModel
import com.newbiechen.nbreader.uilts.mediastore.MediaStoreHelper
import com.github.promeg.pinyinhelper.Pinyin
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 *  author : newbiechen
 *  date : 2019-08-19 16:22
 *  description :
 */

class SmartLookupViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : BaseViewModel() {

    companion object {
        private const val TAG = "SmartLookupViewModel"
    }

    // 是否正在等待
    val isLoading = ObservableField(true)
    // 本地书籍封装组
    val localBookWrapperGroups = ObservableField<List<Pair<String, List<LocalBookWrapper>>>>()

    /**
     * 加载本地书籍
     */
    fun loadLocalBooks(activity: FragmentActivity) {
        // 通知当前正在加载
        isLoading.set(true)

        // 从文件系统获取所有本地书籍信息
        MediaStoreHelper.cursorLocalBooks(activity) { cursorLocalBooks ->
            // 从数据库中获取缓存书籍
            addDisposable(
                // 从数据库中获取本地书籍
                // TODO:还应该加一个判断必须是 localBook
                bookRepository.getBooks(true)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {

                        // 将 List 转换成 SparseArray，优化查找效率
                        val cacheBookMap = LongSparseArray<BookEntity>(it.size)
                        it.forEach { cacheLocalBook ->
                            cacheBookMap.put(cacheLocalBook.id.toLong(), cacheLocalBook)
                        }

                        // 将书籍与所有文件匹配，标记是否添加过缓存。
                        val localBookWrappers: List<LocalBookWrapper> =
                            cursorLocalBooks.map { systemLocalBook ->
                                // 判断 localBook 的 id 是否和缓存列表中的 id 一致
                                val localBookWrapper: LocalBookWrapper =
                                    if (cacheBookMap.contains(systemLocalBook.id)) {
                                        LocalBookWrapper(systemLocalBook, true)
                                    } else {
                                        LocalBookWrapper(systemLocalBook, false)
                                    }

                                localBookWrapper
                            }

                        // TODO:需要传入分类类型,如：根据拼音分类，根据日期分类

                        // 通知设置
                        localBookWrapperGroups.set(createGroupByLetter(localBookWrappers))
                        // 通知更新取消
                        isLoading.set(false)
                    }
            )
        }
    }

    fun deleteLocalBooks(localBookIds: List<Long>) {
        if (localBookIds.isEmpty()) {
            return
        }

        val groups = localBookWrapperGroups.get()
        if (groups != null) {
            for (element in groups) {
                val localBook = (element.second as MutableList<LocalBookEntity>).iterator()
                // 遍历 LocalBook
                while (localBook.hasNext()) {
                    val bookInfo = localBook.next()
                    // 如果包含该 id 则删除
                    if (localBookIds.contains(bookInfo.id)) {
                        localBook.remove()
                    }
                }
            }
            localBookWrapperGroups.set(groups)
        }
    }

    /**
     * 通过文件的的首字母进行分组
     */
    private fun createGroupByLetter(bookEntities: List<LocalBookWrapper>): List<Pair<String, List<LocalBookWrapper>>> {
        // 实现 map 查找，然后转化成 List?，再排序
        // 特殊字符区间
        val letterRange = Range<Char>('A', 'Z')
        var groupMap: MutableMap<Char, MutableList<LocalBookWrapper>> = mutableMapOf()

        bookEntities.forEach {
            val pinyin = Pinyin.toPinyin(it.localBookEntity.name[0])
            // 获取首字母
            var letter = pinyin[0].toUpperCase()
            // 如果 letter 在指定区间外，默认设置为 #
            if (!letterRange.contains(letter)) {
                letter = '#'
            }

            // 如果队列中没有这种类型
            if (groupMap[letter] == null) {
                groupMap[letter] = mutableListOf()
            }

            // 添加到 map 的数组中
            groupMap[letter]!!.add(it)
        }

        // 将 groupMap 中的数据，转化为 Pair
        val groupList: List<Pair<String, List<LocalBookWrapper>>> = groupMap.map {
            Pair<String, List<LocalBookWrapper>>(it.key.toString(), it.value)
        }

        // 进行排序操作
        Collections.sort(groupList) { o1, o2 ->
            if (o1.first > o2.first) 1 else 0
        }

        return groupList
    }
}