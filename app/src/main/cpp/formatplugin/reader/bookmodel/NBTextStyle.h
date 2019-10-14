// author : newbiechen
// date : 2019-09-27 17:51
// description : 文本具有的标记
//

#ifndef NBREADER_TEXTTYPE_H
#define NBREADER_TEXTTYPE_H

/**
 * 文本样式标签
 */
enum class NBTextStyle {
    // all the values MUST be in the range 0..127
    // 不过我从作者上它用的是 unsigned char 类型，应该能标记到 255 ==> 详见 TextModel

    REGULAR = 0, // 常规
    TITLE = 1, // 标题
    SECTION_TITLE = 2, // 单元标题
    POEM_TITLE = 3, // 诗标题
    SUBTITLE = 4, // 副标题
    ANNOTATION = 5, // 注解
    EPIGRAPH = 6, // 题词
    STANZA = 7, //（詩的）節，段
    VERSE = 8, // 诗
    PREFORMATTED = 9, //
    IMAGE = 10, // 图片
    END_OF_SECTION = 11, // 段落的末尾
    CITE = 12, // 引用
    AUTHOR = 13, // 作者
    DATEKIND = 14, // 日期
    INTERNAL_HYPERLINK = 15, // 内联超链接
    FOOTNOTE = 16, // 注脚
    EMPHASIS = 17, // 强调
    STRONG = 18, //
    SUB = 19,
    SUP = 20,
    CODE = 21,
    STRIKETHROUGH = 22, // 删除线
    CONTENTS_TABLE_ENTRY = 23,
    LIBRARY_ENTRY = 25, //
    ITALIC = 27, // 加细
    BOLD = 28, // 加粗
    DEFINITION = 29, // 定义
    DEFINITION_DESCRIPTION = 30, // 定义的描述
    H1 = 31,// Html 的标题
    H2 = 32,
    H3 = 33,
    H4 = 34,
    H5 = 35,
    H6 = 36,
    EXTERNAL_HYPERLINK = 37, // 外部超链接
    XHTML_TAG_P = 51,
};


#endif //NBREADER_TEXTTYPE_H
