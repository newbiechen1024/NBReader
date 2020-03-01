package com.newbiechen.nbreader.ui.component.book.text.entity.resource.image

import com.newbiechen.nbreader.ui.component.book.text.util.TextConstants
import java.io.File

/**
 *  author : newbiechen
 *  date : 2020/2/29 8:22 PM
 *  description :文本图片构建器
 */

object TextImageFactory {

    fun getTextImage(path: String): TextImage? {
        // 是否是 http
        // 检测是否是 ftp
        // 检测是否路径
        File.separator
        // 检测路径是否存在 :，表示 zip
        // TODO:现在暂时只有 zip 的情况
        return if (path.contains(TextConstants.zipSeparator)) {
            TextZipImage(path)
        } else {
            null
        }
    }
}