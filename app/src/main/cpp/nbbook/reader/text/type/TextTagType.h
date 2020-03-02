// author : newbiechen
// date : 2019-12-30 14:55
// description : 文本标签类型
//

#ifndef NBREADER_TEXTTAGTYPE_H
#define NBREADER_TEXTTAGTYPE_H


enum class TextTagType : char {
    TEXT = 1, // 文本标签
    IMAGE = 2, // 图片标签
    CONTROL = 3, // 控制标签
    HYPERLINK_CONTROL = 4, // 超链接控制标签
    STYLE_CSS = 5,
    STYLE_OTHER = 6,
    STYLE_CLOSE = 7,
    FIXED_HSPACE = 8,
    RESET_BIDI = 9,
    AUDIO = 10,
    VIDEO = 11,     // 视频标签
    EXTENSION = 12, // 扩展标签
    PARAGRAPH = 13, // 段落标签
};


#endif //NBREADER_TEXTTAGTYPE_H
