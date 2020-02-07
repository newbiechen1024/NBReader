package com.newbiechen.nbreader.ui.component.book.text.entity

/**
 *  author : newbiechen
 *  date : 2019-10-29 19:13
 *  description :将一个或多个 Element 捆绑成一组内容区域。
 *  举例：超链接是由一组 word 组成的，所以超链接就是一个 TextRegion
 *
 *  TODO：这部分暂时没有用到，只有一个骨架
 */

/**
 * @param regionInterval:内容区域的区间对应 element 的区间
 * @param elementAreas:所有 element area 的列表
 * @param fromAreaIndex:内容区域对应 elementAreas 的起始位置
 */
class TextRegion(
    val regionInterval: TextRegionInterval,
    elementAreas: List<TextElementArea>,
    fromAreaIndex: Int
) {
    private var mElementAreaList = elementAreas
    private var mFromAreaIndex = fromAreaIndex
    private var mToAreaIndex = fromAreaIndex + 1

    /**
     * 扩大 Region 区域
     */
    fun extend() {
        ++mToAreaIndex
    }
}
