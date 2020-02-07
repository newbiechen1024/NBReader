package com.newbiechen.nbreader.ui.component.binding

import android.view.ViewGroup
import android.view.animation.Animation
import androidx.databinding.BindingAdapter

/**
 *  author : newbiechen
 *  date : 2019-08-29 15:59
 *  description :
 */

object ViewGroupBinding {
    @BindingAdapter("app:enterAnim", "app:exitAnim", "app:visible")
    @JvmStatic
    fun ViewGroup.setTransitionAnim(enterAnim: Animation?, exitAnim: Animation?, isVisible: Boolean?) {
        if (isVisible == null) {
            return
        }

        // 退出动画
        if (visibility == ViewGroup.VISIBLE && !isVisible) {
            visibility = ViewGroup.GONE
            if (exitAnim != null) {
                startAnimation(exitAnim)
            }
        } else if (visibility != ViewGroup.VISIBLE && isVisible) {
            visibility = ViewGroup.VISIBLE
            if (enterAnim != null) {
                startAnimation(enterAnim)
            }
        }
    }
}