package com.example.newbiechen.nbreader.ui.page.filesystem

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

/**
 *  author : newbiechen
 *  date : 2019-08-21 17:01
 *  description :
 */
class FileSystemViewModel : ViewModel() {
    val checkedCount = ObservableField(0)
    val isCheckedAll = ObservableField(false)
}