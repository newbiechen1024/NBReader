// author : newbiechen
// date : 2019-09-20 15:11
// description : 
//

#include <FormatPluginApp.h>
#include <util/Logger.h>
#include <reader/textmodel/tag/TextTag.h>
#include <util/Constants.h>
#include "TextModel.h"

TextModel::TextModel(const std::string &id, const std::string &language, const size_t rowSize,
                     const std::string &directoryName,
                     const std::string &fileName,
                     FontManager &fontManager)
        : mId(id),
          mLanguage(language.empty() ? FormatPluginApp::getInstance().language() : language),

          mPghBaseAllocator(std::make_shared<TextCachedAllocator>(rowSize, directoryName, fileName,
                                                                  Constants::SUFFIX_PGH_BASE)),
          mPghDetailAllocator(
                  std::make_shared<TextCachedAllocator>(rowSize, directoryName, fileName,
                                                        Constants::SUFFIX_PGH_DETAIL)),
          mFontManager(fontManager),
          mCurDetailTagPtr(nullptr) {
}

TextModel::TextModel(const std::string &id, const std::string &language,
                     std::shared_ptr<TextCachedAllocator> pghBaseAllocator,
                     std::shared_ptr<TextCachedAllocator> pghDetailAllocator,
                     FontManager &fontManager) : mId(id),
                                                 mLanguage(language.empty()
                                                           ? FormatPluginApp::getInstance().language()
                                                           : language),
                                                 mPghBaseAllocator(pghBaseAllocator),
                                                 mPghDetailAllocator(pghDetailAllocator),
                                                 mFontManager(fontManager),
                                                 mCurDetailTagPtr(nullptr) {

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
 * @param isStartTag：是开放标签，还是闭合标签。 ==> 有些 style 标签不需要闭合。
 *
 * control tag 结构：占用 4 字节，格式为 | entry 类型 | 0 | 样式标签 | 是开放标签还是闭合标签 |
 *
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
 * 4. 标签类型：占用 1 字节 ==> 0 或者是
 */
void TextModel::addControlTag(NBTagStyle style, bool isStartTag) {
    mCurDetailTagPtr = mPghDetailAllocator->allocate(4);
    *mCurDetailTagPtr = (char) TextTagType::CONTROL;
    *(mCurDetailTagPtr + 1) = 0;
    *(mCurDetailTagPtr + 2) = (char) style;
    *(mCurDetailTagPtr + 3) = isStartTag ? 1 : 0;

    // 当前段落 entry 数增加
    (mParagraphs.back()->tagCount)++;
}

void TextModel::addStyleTag(const TextStyleTag &entry, unsigned char depth) {

}

void
TextModel::addStyleTag(const TextStyleTag &entry, const std::vector<std::string> &fontFamilies,
                       unsigned char depth) {

}

void TextModel::addStyleCloseTag() {

}

void TextModel::addHyperlinkControlTag(NBTagStyle textStyle, NBTagHyperlinkType hyperlinkType,
                                       const std::string &label) {

}

void TextModel::addTextTag(const std::string &text) {

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
void TextModel::addTextTag(const std::vector<std::string> &text) {
    if (text.empty()) {
        return;
    }

    // 获取传入文本的长度
    size_t textLength = 0;
    // str 持有 UTF-8 编码的数据，通过 UTF-8 解析文本中有多少个字
    for (const std::string &str : text) {
        textLength = UnicodeUtil::utf8Length(str);
    }

    UnicodeUtil::Ucs2String unicode2Str;

    // 是否是追加数据
    if (mCurDetailTagPtr != nullptr && *mCurDetailTagPtr == (char) TextTagType::TEXT) {
        // 如果当前 Entry 是 TEXT_ENTRY 类型，则通过指针获取当前 entry 持有的文本中长度
        const size_t oldTextLength = TextCachedAllocator::readUInt32(mCurDetailTagPtr + 2);

        // 新的段落长度
        const size_t newTextLength = oldTextLength + textLength;

        // 请求重新分配缓冲区
        // 2 * newTextLength ==> utf-16 中一个字占用 2 个字节。所以空间为 2 * 字数
        mCurDetailTagPtr = mPghDetailAllocator->reallocateLast(mCurDetailTagPtr,
                                                               2 * newTextLength + 6);
        // 将重新计算的长度写入 entry 中
        TextCachedAllocator::writeUInt32(mCurDetailTagPtr + 2, newTextLength);
        // 移动到之前填充文本的位置
        size_t offset = 6 + oldTextLength;

        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUcs2(unicode2Str, *it);
            // 获取转换后的总长度的字数 * 2 (因为使用 UTF-16 编码)
            const size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurDetailTagPtr + offset, &unicode2Str.front(), len);
            unicode2Str.clear();
            // 下一文本的起始偏移位置
            offset += len;
        }
    } else {
        // 创建 text 长度的空间
        mCurDetailTagPtr = mPghDetailAllocator->allocate(2 * textLength + 6);
        // 起始位置为 entry 标记
        *mCurDetailTagPtr = (char) TextTagType::TEXT;
        // 用 0 为分割标记
        *(mCurDetailTagPtr + 1) = 0;
        // 将总长度写入到 entry 中
        TextCachedAllocator::writeUInt32(mCurDetailTagPtr + 2, textLength);
        // 起始文本偏移位置
        size_t offset = 6;
        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUcs2(unicode2Str, *it);
            // 获取转换后的总长度的字节数
            const size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurDetailTagPtr + offset, &unicode2Str.front(), len);
            unicode2Str.clear();

            // 下一文本的起始偏移位置
            offset += len;
        }

        (mParagraphs.back()->tagCount)++;
    }
}

void TextModel::addImageTag(const std::string &id, short vOffset, bool isCover) {

}

void TextModel::addFixedHSpaceTag(unsigned char length) {

}

void TextModel::addVideoTag(const TextVideoTag &entry) {

}

void TextModel::addExtensionTag(const std::string &action,
                                const std::map<std::string, std::string> &data) {

}

void TextModel::addParagraphInternal(TextParagraph *paragraph) {
    if (!mParagraphs.empty()) {
        // 获取最后一个段落
        TextParagraph *lastParagraph = mParagraphs.back();

        // 写入 paragraph 标签缓冲数据
        char *paragraphTag = mPghBaseAllocator->allocate(12);
        *paragraphTag = (char) TextBaseTagType::PARAGRAPH;
        *(paragraphTag + 1) = 0;
        *(paragraphTag + 2) = lastParagraph->type;
        *(paragraphTag + 3) = 0;
        TextCachedAllocator::writeUInt32(paragraphTag + 4, lastParagraph->offset);
        TextCachedAllocator::writeUInt32(paragraphTag + 8, lastParagraph->tagCount);
    }

    const size_t bufferOffset = mPghDetailAllocator->getCurOffset();
    // 初始化 TextParagraph
    // 因为 java 中的 char 占 2 字节，所以需要除以 2。
    paragraph->offset = bufferOffset / 2;
    paragraph->tagCount = 0;

    // 存储每个段落
    mParagraphs.push_back(paragraph);
}

bool TextModel::flush() {
    // 强制将缓冲数据写入到文件

    mPghBaseAllocator->flush();
    mPghDetailAllocator->flush();

    return !mPghBaseAllocator->isFailed() || !mPghDetailAllocator->isFailed();
}

TextPlainModel::TextPlainModel(const std::string &id, const std::string &language,
                               const size_t defaultBufferSize,
                               const std::string &directoryName,
                               const std::string &fileName,
                               FontManager &fontManager) : TextModel(id, language,
                                                                     defaultBufferSize,
                                                                     directoryName, fileName,
                                                                     fontManager) {

}

TextPlainModel::TextPlainModel(const std::string &id, const std::string &language,
                               std::shared_ptr<TextCachedAllocator> pghBaseAllocator,
                               std::shared_ptr<TextCachedAllocator> pghDetailAllocator,
                               FontManager &fontManager)
        : TextModel(id, language, pghBaseAllocator, pghDetailAllocator, fontManager) {
}

TextPlainModel::~TextPlainModel() {

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