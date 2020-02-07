package com.newbiechen.nbreader.uilts.rxbus

/**
 *  author : newbiechen
 *  date : 2020-02-06 21:55
 *  description :RxBus 用到的事件
 */

/**
 * 缓存书籍改变监听
 *
 * 1. 用于 FileSystemActivity 增加或删减的时候，通知 BookShelfBook 进行刷新
 */
class CacheBookChangedEvent : RxEvent