// author : newbiechen
// date : 2019-09-20 15:11
// description : 
//

#include <FormatPluginApp.h>
#include "TextModel.h"
#include "TextEntry.h"

TextModel::TextModel(const std::string &id, const std::string &language, const std::size_t rowSize,
                     const std::string &directoryName, const std::string &fileExtension, FontManager &fontManager)
        : mId(0), mLanguage(language.empty() ? FormatPluginApp::getInstance().language() : language),
          mAllocator(std::make_shared<TextCachedAllocator>(rowSize, directoryName, fileExtension)),
          mCurEntryPointer(0),
          mFontManager(fontManager) {
}

TextModel::TextModel(const std::string &id, const std::string &language, std::shared_ptr<TextCachedAllocator> allocator,
                     FontManager &fontManager) : mId(id),
                                                 mLanguage(language.empty() ? FormatPluginApp::getInstance().language()
                                                                            : language),
                                                 mAllocator(allocator),
                                                 mCurEntryPointer(0),
                                                 mFontManager(fontManager) {

}

TextModel::~TextModel() {
    for (std::vector<TextParagraph *>::const_iterator it = mParagraphs.begin();
         it != mParagraphs.end(); ++it) {
        delete *it;
    }
}

/**
 *
 * @param style：style 标签类型
 * @param isTagStart：是开放标签，还是闭合标签。 ==> 有些 style 标签不需要闭合。
 *
 * control entry 结构：占用 4 字节，格式为 | entry 类型 | 0 | 样式标签 | 是开放标签还是闭合标签 |
 *
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
 * 4. 标签类型：占用 1 字节 ==> 0 或者是
 */
void TextModel::addControlEntry(TextStyle styleTag, bool isTagStart) {
    mCurEntryPointer = mAllocator->allocate(4);

    *mCurEntryPointer = TextParagraphEntry::Type::CONTROL_ENTRY;
    *(mCurEntryPointer + 1) = 0;
    *(mCurEntryPointer + 2) = styleTag;
    *(mCurEntryPointer + 3) = isTagStart ? 1 : 0;

    // 当前段落 entry 数增加
    (mParagraphs.back()->entryCount)++;
}

void TextModel::addStyleEntry(const TextStyleEntry &entry, unsigned char depth) {

}

void TextModel::addStyleEntry(const TextStyleEntry &entry, const std::vector<std::string> &fontFamilies,
                              unsigned char depth) {

}

void TextModel::addStyleCloseEntry() {

}

void TextModel::addHyperlinkControl(TextStyle textStyle, HyperlinkType hyperlinkType, const std::string &label) {

}

void TextModel::addText(const std::string &text) {

}

/**
 *
 * @param text ：段落文本数据
 *
 * TEXT_ENTRY：占用 (6 + 文本字节数) 格式为 | entry 类型 | 未知类型 | 文本字节长度 | 文本内容
 *
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 文本字节长度：占用 4 字节
 * 4. 文本内容：占用文本长度字节。
 */
void TextModel::addTexts(const std::vector<std::string> &text) {
    if (text.empty()) {
        return;
    }

    // 获取传入文本的长度
    size_t textTotalLength = 0;
    for (const std::string &str : text) {
        // 获取该文本对应 UTF-8 编码的长度
        textTotalLength = UnicodeUtil::utf8Length(str);
    }

    UnicodeUtil::Unicode2String unicode2Str;

    // 如果当前元素类型是文本类型
    if (mCurEntryPointer != 0 && *mCurEntryPointer == TextParagraphEntry::TEXT_ENTRY) {
        // 如果当前 Entry 是 TEXT_ENTRY 类型，则通过指针获取当前 entry 持有的文本中长度
        const std::size_t oldTextLength = TextCachedAllocator::readUInt32(mCurEntryPointer + 2);
        // 新的段落长度
        const std::size_t newTextLength = oldTextLength + textTotalLength;

        // 请求重新分配缓冲区
        // 2 * newTextLength ==> 最终输出是 UTF-16 所以应该是 UTF-8 * 2
        mCurEntryPointer = mAllocator->reallocateLast(mCurEntryPointer, 2 * newTextLength + 6);
        // 将重新计算的长度写入 entry 中
        TextCachedAllocator::writeUInt32(mCurEntryPointer + 2, newTextLength);
        // 移动到之前填充文本的位置
        std::size_t offset = 6 + oldTextLength;

        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUnicode2(unicode2Str, *it);
            // 获取转换后的总长度的字节数
            const std::size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurEntryPointer + offset, &unicode2Str.front(), len);
            unicode2Str.clear();

            // 下一文本的起始偏移位置
            offset += len;
        }

        // 设置段落的长度
        mParagraphs.back()->textLength = newTextLength;

    } else {
        // 创建 text 长度的空间
        mCurEntryPointer = mAllocator->allocate(2 * textTotalLength + 6);
        // 起始位置为 entry 标记
        *mCurEntryPointer = TextParagraphEntry::TEXT_ENTRY;
        // 用 0 为分割标记
        *(mCurEntryPointer + 1) = 0;
        // 将总长度写入到 entry 中
        TextCachedAllocator::writeUInt32(mCurEntryPointer + 2, textTotalLength);
        // 起始文本偏移位置
        std::size_t offset = 6;
        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUnicode2(unicode2Str, *it);
            // 获取转换后的总长度的字节数
            const std::size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurEntryPointer + offset, &unicode2Str.front(), len);
            unicode2Str.clear();

            // 下一文本的起始偏移位置
            offset += len;
        }

        (mParagraphs.back()->entryCount)++;

        // 设置段落的长度
        mParagraphs.back()->textLength = textTotalLength;
    }

    mParagraphs.back()->curTotalTextLength += textTotalLength;
}

void TextModel::addImage(const std::string &id, short vOffset, bool isCover) {

}

void TextModel::addFixedHSpace(unsigned char length) {

}

void TextModel::addVideoEntry(const VideoEntry &entry) {

}

void TextModel::addExtensionEntry(const std::string &action, const std::map<std::string, std::string> &data) {

}

void TextModel::flush() {
    mAllocator->flush();
}

/**
 * 创建新段落
 * @param type
 */
void TextPlainModel::createParagraph(TextParagraph::Type type) {
    // 如果参数的文本行，则创建
    TextParagraph *paragraph = new TextParagraph(type);
    addParagraphInternal(paragraph);
}

void TextModel::addParagraphInternal(TextParagraph *paragraph) {
    const std::size_t blockCount = mAllocator->getBufferBlockCount();
    const std::size_t blockOffset = mAllocator->getCurBufferBlockOffset();
    // 初始化 TextParagraph
    paragraph->bufferBlockIndex = (blockCount == 0) ? 0 : (blockCount - 1);
    paragraph->bufferBlockOffset = blockOffset / 2;
    paragraph->entryCount = 0;
    paragraph->textLength = 0;
    paragraph->curTotalTextLength = mParagraphs.empty() ? 0 : (mParagraphs.back()->curTotalTextLength);

    // 存储每个段落
    mParagraphs.push_back(paragraph);
}