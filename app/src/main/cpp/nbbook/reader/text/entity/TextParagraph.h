// author : newbiechen
// date : 2019-12-30 14:32
// description :文本段落信息
#ifndef NBREADER_TEXTPARAGRAPH_H
#define NBREADER_TEXTPARAGRAPH_H

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

    TextParagraph(Type paragraphType = TEXT_PARAGRAPH) : type(paragraphType) {
    }

    // 段落类型
    Type type;
};


#endif //NBREADER_TEXTPARAGRAPH_H
