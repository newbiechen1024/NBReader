package com.example.newbiechen.nbreader.ui.component.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object ImageViewBinding {

    @BindingAdapter("app:url")
    @JvmStatic
    fun ImageView.loadImage(url: String) {
        if (url.isEmpty()) {
            return
        }
        Glide.with(context).load(url).override(200, 200).into(this)
    }
}