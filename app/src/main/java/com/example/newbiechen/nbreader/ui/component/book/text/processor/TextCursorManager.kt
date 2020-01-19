package com.example.newbiechen.nbreader.ui.component.book.text.processor

import android.util.LruCache
import com.example.newbiechen.nbreader.ui.component.book.text.TextModel
import com.example.newbiechen.nbreader.ui.component.book.text.processor.cursor.TextParagraphCursor

/**
 *  author : newbiechen
 *  date : 2019-10-21 17:04
 *  description :文本光标管理器
 */

class TextCursorManager(private val textModel: TextModel) : LruCache<Int, TextParagraphCursor>(200) {
    override fun create(key: Int?): TextParagraphCursor {
        return TextParagraphCursor(
            this,
            textModel,
            key!!
        )
    }
}