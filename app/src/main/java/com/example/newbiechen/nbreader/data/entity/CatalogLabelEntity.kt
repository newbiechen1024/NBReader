package com.example.newbiechen.nbreader.data.entity

import com.google.gson.annotations.SerializedName

/**
 *  author : newbiechen
 *  date : 2019-08-02 20:14
 *  description :目录标签
 */

data class CatalogLabelWrapper(
    @SerializedName("female")
    val female: List<CatalogLabelEntity>,
    @SerializedName("male")
    val male: List<CatalogLabelEntity>,
    val ok: Boolean
)

data class CatalogLabelEntity(
    @SerializedName("gender")
    val gender: String,
    // 标签目录
    @SerializedName("major")
    val major: String,
    // 子标签
    @SerializedName("mins")
    val mins: List<String>
)