package com.example.newbiechen.nbreader.ui.component.book.text.util

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 *  author : newbiechen
 *  date : 2019-10-20 18:14
 *  description :文本缓存读取器
 */

class TextCacheReader(
    private val cacheDir: String,
    private val fileExtension: String,
    blockCount: Int
) {

    private val mCacheBlockList: ArrayList<WeakReference<CharArray>> = ArrayList()

    init {
        mCacheBlockList.addAll(Collections.nCopies(blockCount, WeakReference<CharArray>(null)))
    }


    private fun getCacheFilePath(index: Int): String {
        return cacheDir + File.separator + "." + fileExtension
    }

    fun getBufferBlock(index: Int): CharArray {
        if (index < 0 || index >= mCacheBlockList.size) {
            return CharArray(0)
        }
        // 获取缓冲区中的缓冲块
        var block = mCacheBlockList[index].get()

        if (block == null) {
            try {
                val file = File(getCacheFilePath(index))
                val size = file.length().toInt()

                if (size <= 0) {
                    throw TextCacheReaderException(
                        exceptionMessage(index, "size = $size")
                    )
                }

                block = CharArray(size / 2)

                val reader = InputStreamReader(
                    FileInputStream(file),
                    Charsets.UTF_16LE.name()
                )
                val rd = reader.read(block!!)
                if (rd != block!!.size) {
                    // 异常
                }
                reader.close()
            } catch (e: IOException) {
                throw TextCacheReaderException(
                    exceptionMessage(index, null),
                    e
                )
            }

            mCacheBlockList[index] = WeakReference(block)
        }
        return block
    }

    private fun exceptionMessage(index: Int, extra: String?): String {
        val buffer = StringBuilder("Cannot read " + getCacheFilePath(index))
        if (extra != null) {
            buffer.append("; ").append(extra)
        }
        buffer.append("\n")
        try {
            val dir = File(cacheDir)
            buffer.append("ts = ").append(System.currentTimeMillis()).append("\n")
            buffer.append("dir exists = ").append(dir.exists()).append("\n")
            for (f in dir.listFiles()!!) {
                buffer.append(f.name).append(" :: ")
                buffer.append(f.length()).append(" :: ")
                buffer.append(f.lastModified()).append("\n")
            }
        } catch (t: Throwable) {
            buffer.append(t.javaClass.name)
            buffer.append("\n")
            buffer.append(t.message)
        }

        return buffer.toString()
    }
}

class TextCacheReaderException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, throwable: Throwable) : super(message, throwable)
}