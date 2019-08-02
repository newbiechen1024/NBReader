package com.example.newbiechen.nbreader.ui.page.main

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.example.newbiechen.nbreader.ui.page.base.RxViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {

    // 当前页面的位置
    val curPagePos = ObservableField<Int>(0)
    // 当前页面的 title
    val curPageTitle = ObservableField<String>()
}