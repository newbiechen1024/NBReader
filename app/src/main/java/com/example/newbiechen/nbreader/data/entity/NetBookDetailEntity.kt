package com.example.newbiechen.nbreader.data.entity

/**
 *  author : newbiechen
 *  date : 2019-08-14 14:27
 *  description :
 */

data class NetBookDetailWrapper(
    val _id: String,
    val author: String,
    val chaptersCount: Int,
    val contentType: String,
    val cover: String,
    val latelyFollower: Int,
    val isSerial: Boolean,
    val lastChapter: String,
    val longIntro: String,
    val majorCate: String,
    val minorCate: String,
    val retentionRatio: String,
    val rating: BookRating,
    val tags: List<String>,
    val title: String,
    val updated: String,
    val wordCount: Int
)

data class BookRating(
    val count: Int,
    val score: Double,
    val tip: String
)