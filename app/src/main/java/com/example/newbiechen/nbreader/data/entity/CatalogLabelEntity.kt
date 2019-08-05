package com.example.newbiechen.nbreader.data.entity

/**
 *  author : newbiechen
 *  date : 2019-08-02 20:14
 *  description :目录标签
 */

data class CatalogLabelWrapper(
    val female: List<CatalogLabelEntity>,
    val male: List<CatalogLabelEntity>,
    val ok: Boolean
)

data class CatalogLabelEntity(
    // 标签类型：男生女生
    val gender: String,
    // 标签目录
    val major: String,
    // 子标签
    val mins: List<String>
)