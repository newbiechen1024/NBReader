package com.newbiechen.nbreader.ui.component.book.text.processor

import com.newbiechen.nbreader.ui.component.book.text.entity.*
import com.newbiechen.nbreader.ui.component.book.text.entity.element.TextWordElement
import java.util.*

/**
 *  author : newbiechen
 *  date : 2019-10-25 19:32
 *  description :文本区域列表
 */

class TextElementAreaVector {
    // 存储了页面 Element 的显示区域
    private val mAreaList = ArrayList<TextElementArea>()

    // 存储了页面的片段显示区域
    private val myElementRegions = ArrayList<TextRegion>()
    private var myCurrentElementRegion: TextRegion? = null

    fun clear() {
        myElementRegions.clear()
        myCurrentElementRegion = null
        mAreaList.clear()
    }

    fun size(): Int {
        return mAreaList.size
    }

    fun areas(): List<TextElementArea> {
        return ArrayList<TextElementArea>(mAreaList)
    }

    fun getFirstArea(): TextElementArea? {
        return if (mAreaList.isEmpty()) null else mAreaList[0]
    }

    fun getLastArea(): TextElementArea? {
        return if (mAreaList.isEmpty()) null else mAreaList[mAreaList.size - 1]
    }

    fun add(area: TextElementArea): Boolean {
        synchronized(mAreaList) {
            // 如果要加入的 area 在当前 Region 可以容纳区域内
            if (myCurrentElementRegion != null && myCurrentElementRegion!!.regionInterval.isContain(
                    area
                )
            ) {
                // 则 Region 扩大一位。
                myCurrentElementRegion!!.extend()
            } else {
                var regionInterval: TextRegionInterval? = null
                if (area.element is TextWordElement && !area.element.isASpace()) {
                    regionInterval = TextWordRegionInterval(area.element, area)
                }
                // 将最后一个添加的 Area 设置为 myCurrentElementRegion，并添加到 myElementRegions 中
                if (regionInterval != null) {
                    // 每个 Soul 都会创建一个 Region
                    // 传入 Area 在整个书籍的位置索引
                    // 传入整个页面的 Area 列表
                    // 传入 Area 在当前列表的索引
                    myCurrentElementRegion = TextRegion(regionInterval, mAreaList, mAreaList.size)
                    myElementRegions.add(myCurrentElementRegion!!)
                } else {
                    myCurrentElementRegion = null
                }
            }
            return mAreaList.add(area)
        }
    }

    fun getFirstAfter(position: TextPosition?): TextElementArea? {
        if (position == null) {
            return null
        }
        for (area in mAreaList) {
            if (position <= area) {
                return area
            }
        }
        return null
    }

    fun getLastBefore(position: TextPosition?): TextElementArea? {
        if (position == null) {
            return null
        }
        for (i in mAreaList.indices.reversed()) {
            val area = mAreaList[i]
            if (position > area) {
                return area
            }
        }
        return null
    }

    // 根据位置二分查找 TextElementArea
    fun binarySearch(x: Int, y: Int): TextElementArea? {
        synchronized(mAreaList) {
            var left = 0
            var right = mAreaList.size
            while (left < right) {
                val middle = (left + right) / 2
                val candidate = mAreaList[middle]
                when {
                    candidate.startY > y -> right = middle
                    candidate.endY < y -> left = middle + 1
                    candidate.startX > x -> right = middle
                    candidate.endX < x -> left = middle + 1
                    else -> return candidate
                }
            }
            return null
        }
    }

    fun getRegion(interval: TextRegionInterval?): TextRegion? {
        if (interval == null) {
            return null
        }
        for (region in myElementRegions) {
            if (interval == region.regionInterval) {
                return region
            }
        }
        return null
    }
}