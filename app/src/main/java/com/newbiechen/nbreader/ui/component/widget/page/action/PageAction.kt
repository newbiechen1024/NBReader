package com.newbiechen.nbreader.ui.component.widget.page.action

import com.newbiechen.nbreader.ui.component.widget.page.PageType

/**
 *  author : newbiechen
 *  date : 2019-09-02 10:54
 *  description :页面行为的集合
 */

// 页面行为标记
interface PageAction

// 翻页事件
data class TurnPageAction(
    val pageType: PageType
) : PageAction

// 点击页面菜单事件
class TapMenuAction : PageAction