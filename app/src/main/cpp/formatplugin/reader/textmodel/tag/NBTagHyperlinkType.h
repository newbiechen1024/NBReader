// author : newbiechen
// date : 2019-09-27 18:02
// description :  NBReader 支持的 Hyperlink 类型
//

#ifndef NBREADER_HYPERLINKTYPE_H
#define NBREADER_HYPERLINKTYPE_H


enum class NBTagHyperlinkType : char {
    HYPERLINK_NONE = 0,
    HYPERLINK_INTERNAL = 1, // 内部超链接
    HYPERLINK_FOOTNOTE = 2, // 注脚超链接
    HYPERLINK_EXTERNAL = 3, // 外部超链接
};


#endif //NBREADER_HYPERLINKTYPE_H
