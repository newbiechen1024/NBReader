package com.newbiechen.nbreader.ui.component.book.text.processor.textmodel

/**
 *  author : newbiechen
 *  date : 2020-02-10 14:23
 *  description :测试用的 TextModel
 */

class TestTextModel(private val builder: Builder) {

    init {
        // 根据 Builder 初始化操作
    }


    companion object {
        class Builder {
            // 设置书籍缓存地址
            fun setBookCachePath() {

            }

            fun setBookInitChapterTitle() {

            }

            fun setBookResource() {

            }

            fun setBookTitle() {

            }

            fun setBookStragegory() {

            }

            fun setStateStrageory() {}

            fun build() {
                // 生成指定类型的 TextModel，
            }

            // TODO:章节缓存策略也可以放出来实现
        }
    }
}