package com.newbiechen.nbreader.ui.component.widget.page.anim

import android.view.View
import com.newbiechen.nbreader.ui.component.widget.page.text.TextPageManager

/**
 *  author : newbiechen
 *  date : 2020/3/7 10:02 AM
 *  description :
 */
abstract class TextPageAnimation(view: View, pageManager: TextPageManager) : IPageAnimation {
    protected val mView = view
    protected val mPageManager = pageManager
}