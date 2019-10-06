// author : newbiechen
// date : 2019-09-27 18:34
// description : 
//

#ifndef NBREADER_TEXTPARAGRAPH_H
#define NBREADER_TEXTPARAGRAPH_H

#include <stddef.h>

// 默认段落
class TextParagraph {

public:
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

protected:
    TextParagraph() : mEntryNumber(0) {
    }

public:
    virtual ~ZLTextParagraph() {
    }

    virtual Type getType() const {
        return TEXT_PARAGRAPH;
    }

    size_t getEntryNumber() const {
        return mEntryNumber;
    }

private:
    void addEntry(char *address) {
        if (mEntryNumber == 0) {
            mFirstEntryAddress = address;
        }
        ++mEntryNumber;
    }

private:
    // 第一个元素地址
    char *mFirstEntryAddress;
    // 元素类型 Number
    size_t mEntryNumber;

    friend class TextModel;

    // TODO：待定
    friend class TextPlainModel;
};

// 可指定类型段落
class TextSpecialParagraph : public TextParagraph {

private:
    TextSpecialParagraph(Type type) : mType(type) {
    }

public:
    ~ZLTextSpecialParagraph() {
    }

    Type getType() const {
        return mType;
    }

private:
    Type mType;

    // TODO:对应 ZLTextPlainModel ==> 这个还没实现
    friend class TextPlainModel;
};


#endif //NBREADER_TEXTPARAGRAPH_H
