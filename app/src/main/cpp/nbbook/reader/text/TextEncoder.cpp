// author : newbiechen
// date : 2019-12-30 13:22
// description : 
//

#include "TextEncoder.h"
#include "../../util/Logger.h"
#include "type/TextTagType.h"

static const size_t BUFFER_SIZE = 8192;

static const std::string TAG = "TextEncoder";

TextEncoder::TextEncoder() : mBufferAllocatorPtr(nullptr),
                             mCurParagraphPtr(nullptr),
                             mCurTagPtr(nullptr) {
    mIsOpen = false;
    mCurParagraphCount = 0;
}

TextEncoder::~TextEncoder() {
    release();
}

// TODO:不考虑多线程的情况
void TextEncoder::open() {
    Logger::i(TAG, "open");

    // 如果已经打开了，则默认返回 true
    if (isOpen()) {
        return;
    }

    // 如果缓冲区不存在已存在则没有必要重打开
    mBufferAllocatorPtr = new TextBufferAllocator(BUFFER_SIZE);
    mIsOpen = true;
}

size_t TextEncoder::close(char **outBuffer) {
    Logger::i(TAG, "close");

    // 如果没有开启，则返回 -1，表示关闭失败
    if (!isOpen()) {
        return -1;
    }

    // 先进行强制缓冲
    flush();

    // 将 allocator 中的数据输出
    size_t result = mBufferAllocatorPtr->close(outBuffer);

    // 释放无用资源操作
    release();

    return result;
}

void TextEncoder::release() {
    if (mBufferAllocatorPtr != nullptr) {
        delete mBufferAllocatorPtr;
        mBufferAllocatorPtr = nullptr;
    }

    if (mCurParagraphPtr != nullptr) {
        delete mCurParagraphPtr;
        mCurParagraphPtr = nullptr;
    }

    mCurTagPtr = nullptr;

    // 设置当前编码器状态
    mIsOpen = false;
}

void TextEncoder::checkEncoderState() {
    // 如果编码器没有 open 就处理，抛出异常
    if (!isOpen()) {
        // TODO:抛出异常，暂时为直接崩溃
        exit(-1);
    }
}

void TextEncoder::checkTagState() {
    // 如果编码器没有 open 处理，或者没有调用 createParagraph 都抛出异常
    checkEncoderState();

    if (mCurParagraphPtr == nullptr) {
        exit(-1);
    }
}

void TextEncoder::createParagraph(TextParagraph::Type paragraphType) {

    // 检测编码器状态
    checkEncoderState();

    // 强制将数据输出到缓冲区
    flush();

    // 创建新的段落
    mCurParagraphPtr = new TextParagraph(paragraphType);

}

void TextEncoder::flush() {
    if (mCurParagraphPtr == nullptr) {
        return;
    }

    // 获取上一个段落
    TextParagraph *lastParagraphPtr = mCurParagraphPtr;

    // 将上一个段落数据写入到缓冲区中，一个 paragraph 占 4 字节
    char *paragraphTag = mBufferAllocatorPtr->allocate(4);

    *paragraphTag = (char) TextTagType::PARAGRAPH;
    *(paragraphTag + 1) = 0;
    *(paragraphTag + 2) = lastParagraphPtr->type;
    *(paragraphTag + 3) = 0;

    // 数据已经存储到缓冲区，释放段落指针
    delete mCurParagraphPtr;

    mCurParagraphPtr = nullptr;

    mCurParagraphCount++;
}

/**
 *
 * @param text ：段落文本数据
 *
 * TEXT_TAG：占用 (6 + 文本字节数) 格式为 | entry 类型 | 未知类型 | 文本字节长度 | 文本内容
 *
 * 1. tag 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 文本字节长度：占用 4 字节
 * 4. 文本内容：占用文本长度字节。
 */
void TextEncoder::addTextTag(const std::vector<std::string> &text) {

    // 检测添加 tag 的环境
    checkTagState();

    // 如果传入的文本信息，为空则返回
    if (text.empty()) {
        return;
    }

    // 文本中含有多少字符
    size_t wordCount = 0;
    // str 持有 UTF-8 编码的数据，通过 UTF-8 解析文本中有多少个字符
    for (const std::string &str : text) {
        wordCount = UnicodeUtil::utf8Length(str);
        Logger::i(TAG, "addTextTag: str = " + str);
    }

    UnicodeUtil::Ucs2String unicode2Str;

    //  TODO:这部分不需要使用 utf-16 转码，直接使用 utf-8 存储

    // 是否是追加数据
    if (mCurTagPtr != nullptr && *mCurTagPtr == (char) TextTagType::TEXT) {
        // 如果当前 Entry 是 TEXT_ENTRY 类型，则通过指针获取当前 entry 持有的文本中长度
        const size_t oldWordCount = TextBufferAllocator::readUInt32(mCurTagPtr + 2);

        // 新的段落长度
        const size_t newWordCount = oldWordCount + wordCount;

        // 请求重新分配缓冲区
        // 2 * newTextLength ==> utf-16 中一个字占用 2 个字节。所以空间为 2 * 字数
        mCurTagPtr = mBufferAllocatorPtr->reallocateLast(mCurTagPtr, 2 * newWordCount + 6);
        // 将重新计算的长度写入 entry 中
        TextBufferAllocator::writeUInt32(mCurTagPtr + 2, 2 * newWordCount);

        // TODO:这里和 FBReader有出入，我认为应该 *2,不知道对不对
        // 偏移 6 字节，以及上一段数据的位置
        size_t offset = 6 + oldWordCount * 2;

        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUcs2(unicode2Str, *it);
            // 获取转换后的总长度的字数 * 2 (因为使用 UTF-16 编码)
            const size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurTagPtr + offset, &unicode2Str.front(), len);
            unicode2Str.clear();
            // 下一文本的起始偏移位置
            offset += len;
        }
    } else {
        // 创建 text 长度的空间
        mCurTagPtr = mBufferAllocatorPtr->allocate(2 * wordCount + 6);
        // 起始位置为 entry 标记
        *mCurTagPtr = (char) TextTagType::TEXT;
        // 用 0 为分割标记
        *(mCurTagPtr + 1) = 0;
        // 将总长度写入到 entry 中
        TextBufferAllocator::writeUInt32(mCurTagPtr + 2, 2 * wordCount);
        // 偏移 6 字节，指向 TextTag 的文本内容赋值位置

        size_t offset = 6;
        for (std::vector<std::string>::const_iterator it = text.begin(); it != text.end(); ++it) {
            // 将 utf-8 转换成 unicode2
            UnicodeUtil::utf8ToUcs2(unicode2Str, *it);
            // 获取转换后的总长度的字节数
            const size_t len = 2 * unicode2Str.size();
            // 进行复制操作
            std::memcpy(mCurTagPtr + offset, &unicode2Str.front(), len);
            unicode2Str.clear();
            // 下一文本的起始偏移位置
            offset += len;
        }
    }
}

/**
 *
 * @param style：style 标签类型
 * @param isStartTag：是开放标签，还是闭合标签。 ==> 有些 style 标签不需要闭合。
 *
 * control tag 结构：占用 4 字节，格式为 | entry 类型 | 0 | 样式标签 | 是开放标签还是闭合标签 |
 *
 * 1. tag 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
 * 4. 标签类型：占用 1 字节 ==> 0 或者是
 */
void TextEncoder::addControlTag(TextKind kind, bool isStartTag) {

    mCurTagPtr = mBufferAllocatorPtr->allocate(4);
    *mCurTagPtr = (char) TextTagType::CONTROL;
    *(mCurTagPtr + 1) = 0;
    *(mCurTagPtr + 2) = (char) kind;
    *(mCurTagPtr + 3) = isStartTag ? 1 : 0;
}

/**
 *
 * @param length：竖直距离
 *
 * 1. tag 类型：占用 1 字节
 * 2. 对齐填充：占用 1 字节
 * 3. 竖直距离：占用 1 字节
 * 4. 对齐填充：占用 1 字节
 */
void TextEncoder::addFixedHSpace(unsigned char length) {
    mCurTagPtr = mBufferAllocatorPtr->allocate(4);
    *mCurTagPtr = (char) TextTagType::FIXED_HSPACE;
    *(mCurTagPtr + 1) = 0;
    *(mCurTagPtr + 2) = length;
    *(mCurTagPtr + 3) = 0;
/*    myParagraphs.back()->addEntry(mCurTagPtr);
    ++myParagraphLengths.back();*/
}

void TextEncoder::addStyleTag(const TextStyleTag &tag, unsigned char depth) {
    addStyleTag(tag, tag.fontFamilies(), depth);
}


/**
 *
 * @param TextStyleTag：样式信息
 * @param fontFamilies：字体资源
 * @param depth：样式的深度
 *
 * 1. style 类型：占 1 字节。 style 类型有 StyleCss 和 StyleOther 两种
 * 2. depth 信息：占 1 字节。 style 深度。
 * 3. featureMask 信息：占 4 字节。style 包含的样式标记。样式类型详见 TextFeature
 * 4. 对于 TextFeature 标记的前 9 种类型，是否被标记，如果被标记，则填充样式数值信息：占 4 字节。
 * 5. 对于 TextFeature 标记的后 3 种类型，是否被标记，如果被标记，则填充样式数值信息：占 2 字节。
 */
void TextEncoder::addStyleTag(const TextStyleTag &tag, const std::vector<std::string> &fontFamilies,
                              unsigned char depth) {
    // 基础标记长度
    std::size_t len = 6; // entry type + feature mask

    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        if (tag.isFeatureSupported((TextFeature) i)) {
            len += 4; // each supported length
        }
    }

    if (tag.isFeatureSupported(TextFeature::ALIGNMENT_TYPE) ||
        tag.isFeatureSupported(TextFeature::NON_LENGTH_VERTICAL_ALIGN)) {
        len += 2;
    }
    if (tag.isFeatureSupported(TextFeature::FONT_FAMILY)) {
        len += 2;
    }
    if (tag.isFeatureSupported(TextFeature::FONT_STYLE_MODIFIER)) {
        len += 2;
    }


    mCurTagPtr = mBufferAllocatorPtr->allocate(len);

    char *address = mCurTagPtr;

    *address++ = (char) tag.entryKind();
    *address++ = 0;
    *address++ = depth;
    *address++ = 0;

    address = TextBufferAllocator::writeUInt16(address, tag.myFeatureMask);

    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        if (tag.isFeatureSupported((TextFeature) i)) {
            const TextStyleTag::LengthType &len = tag.myLengths[i];
            address = TextBufferAllocator::writeUInt16(address, len.Size);
            *address++ = (char) len.Unit;
            *address++ = 0;
        }
    }

    if (tag.isFeatureSupported(TextFeature::ALIGNMENT_TYPE) ||
        tag.isFeatureSupported(TextFeature::NON_LENGTH_VERTICAL_ALIGN)) {
        *address++ = (char) tag.myAlignmentType;
        *address++ = tag.myVerticalAlignCode;
    }

    if (tag.isFeatureSupported(TextFeature::FONT_FAMILY)) {
        // TODO:暂时不处理字体信息，设置使用的 family 在资源文件中的索引
        address = TextBufferAllocator::writeUInt16(address,
                /*myFontManager.familyListIndex(fontFamilies)*/0);
    }

    if (tag.isFeatureSupported(TextFeature::FONT_STYLE_MODIFIER)) {
        *address++ = tag.mySupportedFontModifier;
        *address = tag.myFontModifier;
    }
/*    // --- writing entry

    myParagraphs.back()->addEntry(myLastEntryStart);
    ++myParagraphLengths.back();*/
}

void TextEncoder::addStyleCloseTag() {
    mCurTagPtr = mBufferAllocatorPtr->allocate(2);

    char *address = mCurTagPtr;

    *address++ = (char) TextTagType::STYLE_CLOSE;
    *address = 0;

/*    myParagraphs.back()->addEntry(myLastEntryStart);
    ++myParagraphLengths.back();*/
}

void TextEncoder::addHyperlinkControlTag(TextKind kind, const std::string &label) {
    // TODO：占位标记，暂未实现
    mCurTagPtr = mBufferAllocatorPtr->allocate(2);

    char *address = mCurTagPtr;

    *address++ = (char) TextTagType::HYPERLINK_CONTROL;
    *address = 0;

/*    ZLUnicodeUtil::Ucs2String ucs2label;
    ZLUnicodeUtil::utf8ToUcs2(ucs2label, label);

    const std::size_t len = ucs2label.size() * 2;

    myLastEntryStart = myAllocator->allocate(len + 6);
    *myLastEntryStart = ZLTextParagraphEntry::HYPERLINK_CONTROL_ENTRY;
    *(myLastEntryStart + 1) = 0;
    *(myLastEntryStart + 2) = textKind;
    *(myLastEntryStart + 3) = hyperlinkType;
    ZLCachedMemoryAllocator::writeUInt16(myLastEntryStart + 4, ucs2label.size());
    std::memcpy(myLastEntryStart + 6, &ucs2label.front(), len);
    myParagraphs.back()->addEntry(myLastEntryStart);
    ++myParagraphLengths.back();*/
}

void TextEncoder::addImageTag() {
    // TODO：占位标记，暂未实现
    mCurTagPtr = mBufferAllocatorPtr->allocate(2);

    char *address = mCurTagPtr;

    *address++ = (char) TextTagType::IMAGE;
    *address = 0;
}

void TextEncoder::addVideoTag() {

}