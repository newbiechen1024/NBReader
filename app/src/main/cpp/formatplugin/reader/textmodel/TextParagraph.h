// author : newbiechen
// date : 2019-09-27 18:34
// description : 段落结构封装，包含段落在缓冲区的位置、段落块数据信息

#ifndef NBREADER_TEXTPARAGRAPH_H
#define NBREADER_TEXTPARAGRAPH_H

#include <stddef.h>

// 默认段落
struct TextParagraph {
    // 段落类型
    enum Type {
        TEXT_PARAGRAPH = 0, // 文本段落
        TREE_PARAGRAPH = 1, // 目录段落
        EMPTY_LINE_PARAGRAPH = 2, // 空行段落
        BEFORE_SKIP_PARAGRAPH = 3,
        AFTER_SKIP_PARAGRAPH = 4,
        END_OF_SECTION_PARAGRAPH = 5,
        PSEUDO_END_OF_SECTION_PARAGRAPH = 6,
        END_OF_TEXT_PARAGRAPH = 7,
        ENCRYPTED_SECTION_PARAGRAPH = 8,
    };

    TextParagraph(Type paragraphType = TEXT_PARAGRAPH) : type(paragraphType),
                                                         offset(0),
                                                         tagCount(0) {
    }

    // 段落类型
    Type type;

    // 段落在 .pgd 文件中的起始位置偏移
    int offset;

    // 段落中包含的标签数量
    int tagCount;
};

#endif //NBREADER_TEXTPARAGRAPH_H
