package com.newbiechen.nbreader.ui.component.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView

// 设置 Image 居中
// 设置 getImage 抽象类
// 设置 onCheckedChange 抽象类
abstract class TabView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    val isChecked: Boolean get() = isViewChecked


    private var mIvIcon: ImageView = ImageView(context)

    private var isViewChecked: Boolean = false

    init {
        // 添加 image View
        val layoutParams: LayoutParams =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        layoutParams.gravity = Gravity.CENTER

        mIvIcon.setImageResource(getImageRes(isViewChecked))

        // 将 ImageView 添加到 FrameLayout 中
        addView(mIvIcon, layoutParams)
    }

    abstract fun getImageRes(isChecked: Boolean): Int

    abstract fun onCheckedChange(isChecked: Boolean)

    // 监听改变
    fun setChecked(checked: Boolean) {
        if (isViewChecked == checked) {
            return
        }
        isViewChecked = checked
        // 更新图片
        mIvIcon.setImageResource(getImageRes(isViewChecked))
        // 通知改变
        onCheckedChange(isViewChecked)
    }
}