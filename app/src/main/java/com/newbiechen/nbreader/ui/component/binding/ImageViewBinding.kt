package com.newbiechen.nbreader.ui.component.binding

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.newbiechen.nbreader.R

object ImageViewBinding {

    @BindingAdapter("app:url")
    @JvmStatic
    fun ImageView.loadImage(url: String?) {
        if (TextUtils.isEmpty(url)) {
            return
        }

        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ic_image_loading)
            .error(R.drawable.ic_image_load_error)
            .override(200, 200)
            .into(this)
    }
}