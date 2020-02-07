package com.newbiechen.nbreader.ui.component.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newbiechen.nbreader.data.entity.CatalogEntity
import com.newbiechen.nbreader.ui.component.adapter.FindAdapter
import com.newbiechen.nbreader.ui.component.adapter.LocalBookWrapper
import com.newbiechen.nbreader.ui.component.adapter.SmartLookupAdapter

object RecyclerViewBinding {

    @BindingAdapter("app:items")
    @JvmStatic
    fun RecyclerView.setCatalogs(catalogs: List<CatalogEntity>?) {
        (adapter as FindAdapter?)?.apply {
            refreshItems(catalogs)
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun RecyclerView.setBookInfoGroup(groups: List<Pair<String, List<LocalBookWrapper>>>?) {
        if (groups == null) {
            return
        }

        (adapter as SmartLookupAdapter?)?.apply {
            refreshAllGroup(groups)
        }
    }
}