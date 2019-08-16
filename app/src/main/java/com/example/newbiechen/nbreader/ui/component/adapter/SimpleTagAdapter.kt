package com.example.newbiechen.nbreader.ui.component.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.newbiechen.nbreader.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

/**
 *  author : newbiechen
 *  date : 2019-08-16 16:44
 *  description :
 */

class SimpleTagAdapter(datas: List<String>?) : TagAdapter<String>(datas) {
    override fun getView(parent: FlowLayout?, position: Int, t: String?): View {
        val tvTag = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.item_tag, parent, false) as TextView
        tvTag.text = t
        return tvTag
    }
}