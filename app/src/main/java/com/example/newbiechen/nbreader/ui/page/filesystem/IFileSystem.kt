package com.example.newbiechen.nbreader.ui.page.filesystem

/**
 *  author : newbiechen
 *  date : 2019-08-21 15:48
 *  description :
 */

interface IFileSystem {
    fun setCheckedAll(isChecked: Boolean)

    fun getCheckedCount(): Int

    fun deleteCheckedAll()

    fun getCheckedFile(): List<String>

    fun setFileCallback(callback: IFileSystemCallback)

    fun getFileCount(): Int
}

interface IFileSystemCallback {
    fun onCheckedChange(isChecked: Boolean)
}