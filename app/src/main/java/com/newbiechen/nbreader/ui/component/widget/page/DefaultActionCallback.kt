package com.newbiechen.nbreader.ui.component.widget.page

import com.newbiechen.nbreader.ui.component.widget.page.action.PageAction
import com.newbiechen.nbreader.ui.component.widget.page.action.TapMenuAction
import com.newbiechen.nbreader.ui.component.widget.page.text.PageActionListener

/**
 *  author : newbiechen
 *  date : 2020/3/7 8:59 PM
 *  description :页面行为事件回调
 */

open class DefaultActionCallback : PageActionListener {
    override fun invoke(action: PageAction) {
        when (action) {
            is TapMenuAction -> onTapMenuAction(action)
            else -> onUnknownAction(action)
        }
    }

    open fun onTapMenuAction(action: TapMenuAction) {

    }

    open fun onUnknownAction(action: PageAction) {

    }
}