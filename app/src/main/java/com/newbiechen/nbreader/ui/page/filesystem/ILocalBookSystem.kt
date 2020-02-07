package com.newbiechen.nbreader.ui.page.filesystem

import com.newbiechen.nbreader.data.entity.LocalBookEntity


/**
 *  author : newbiechen
 *  date : 2019-08-21 15:48
 *  description :本地书籍系统
 */

interface ILocalBookSystem {
    /**
     * 设置全选
     * @param isChecked:是否全选
     */
    fun setBookCheckedAll(isChecked: Boolean)

    /**
     * 获取选中书籍的数量
     */
    fun getCheckedBookCount(): Int

    /**
     * 删除选中的书籍
     */
    fun deleteCheckedBooks()

    /**
     * 获取选中的书籍
     */
    fun getCheckedBooks(): List<LocalBookEntity>

    /**
     * 设置书籍回调
     */
    fun setBookCallback(callback: ILocalBookCallback)

    /**
     * 获取书籍数
     */
    fun getBookCount(): Int

    /**
     * 存储选中的书籍
     */
    fun saveCheckedBooks()
}

interface ILocalBookCallback {
    fun onCheckedChange(isChecked: Boolean)
    fun onSaveCheckedBooks(localBooks: List<LocalBookEntity>)
    fun onDeleteCheckedBooks(localBooks: List<LocalBookEntity>)
}