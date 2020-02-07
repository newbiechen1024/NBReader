package com.newbiechen.nbreader.data.entity

/**
 *  author : newbiechen
 *  date : 2019-08-05 19:51
 *  description :网络书籍列表数据
 */

/**
 *
{
"ok": true,
"books": [
{
"_id": "5d2ea9c9933792125cd1964e",
"hasCp": true,
"title": "蜜汁炖鱿鱼（亲爱的，热爱的）",
"aliases": "",
"cat": "现代言情",
"author": "墨宝非宝",
"site": "zhuishuvip",
"cover": "/agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F3389912%2F3389912_ea6bd0db8830462b9a954d7deb0523e0.jpg%2F",
"shortIntro": "（热播电视剧《亲爱的，热爱的》原著）：身为翻唱界的软萌小天后，佟年从来没想到她会对一个人一见钟情。她以为她追的是个三次元帅哥，不想，此人却是电竞圈的远古传说。“韩商言”，她微微仰着头，“我喜欢你。”喜欢到恨不得一天有二十五个小时能和你在一起，就黏着你，看你生气，看你笑，看你发脾气，看你认真工作……他用几乎不能听到的声音，回答她：“听到了。”佟年，你有多想得到我，我就有多想要你。除了你，谁都不行。我韩商言从来不是个外露的人，感情都在心里，浪漫什么的，不需要。一辈子那么长，我都给你。",
"lastChapter": "第二十七章 番外：酒X酒",
"retentionRatio": 58.86,
"banned": 0,
"allowMonthly": false,
"latelyFollower": 21944,
"wordCount": 310857,
"contentType": "epub",
"superscript": "",
"sizetype": -1,
"highlight": {},
"majorCate": "现代言情",
"minorCate": "都市情缘",
"weight": 8.516,
"isConfig": false,
"allowFree": false
}],
"total":746
}
 **/

data class NetBookListWrapper(
    val books: List<NetBookEntity>,
    val ok: Boolean,
    val total: Int
)

data class NetBookEntity(
    val _id: String,
    val author: String,
    val cat: String,
    val contentType: String,
    val cover: String,
    val hasCp: Boolean,
    val lastChapter: String,
    val latelyFollower: Int,
    val majorCate: String,
    val minorCate: String,
    val retentionRatio: Double,
    val shortIntro: String,
    val site: String,
    val sizetype: Int,
    val title: String,
    val wordCount: Int
) {
    override fun toString(): String {
        return "BookEntity(_id='$_id', author='$author', cat='$cat', contentType='$contentType', cover='$cover', hasCp=$hasCp, lastChapter='$lastChapter', latelyFollower=$latelyFollower, majorCate='$majorCate', minorCate='$minorCate', retentionRatio=$retentionRatio, shortIntro='$shortIntro', site='$site', sizetype=$sizetype, title='$title', wordCount=$wordCount)"
    }
}