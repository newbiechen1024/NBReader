package com.example.newbiechen.nbreader.ui.page.booklist

import androidx.databinding.ObservableField
import com.example.newbiechen.nbreader.ui.page.base.RxViewModel

class BookListViewModel: RxViewModel() {
    var selectedSortPos = ObservableField(0)
}