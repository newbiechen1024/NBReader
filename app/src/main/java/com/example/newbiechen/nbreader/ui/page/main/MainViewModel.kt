package com.example.newbiechen.nbreader.ui.page.main

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
    // 当前页面的位置
    val mCurPagePos = ObservableField<Int>(0)
}