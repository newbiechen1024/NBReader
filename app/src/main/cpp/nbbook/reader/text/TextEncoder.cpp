// author : newbiechen
// date : 2019-12-30 13:22
// description : 
//

#include "TextEncoder.h"
#include "../../util/Logger.h"
#include "type/TextTagType.h"
#include "tag/ParagraphTag.h"
#include "tag/ContentTag.h"

static const size_t BUFFER_SIZE = 8192;

static const std::string TAG = "TextEncoder";

TextEncoder::TextEncoder() : mParcelBuffer(nullptr),
                             mCurParagraphPtr(nullptr),
                             mContentTagPtr(nullptr),
                             mParcel(nullptr) {
    mIsOpen = false;
    mCurParagraphCount = 0;
}

TextEncoder::~TextEncoder() {
    release();
}

// TODO:不考虑多线程的情况
void TextEncoder::open() {
    // 如果已经打开了，则默认返回 true
    if (isOpen()) {
        return;
    }

    // 如果缓冲区不存在已存在则没有必要重打开
    mParcelBuffer = new ParcelBuffer(BUFFER_SIZE);
    mParcel = new Parcel(mParcelBuffer);

    mIsOpen = true;
}

size_t TextEncoder::close(char **outBuffer) {
    Logger::i(TAG, "close");

    // 如果没有开启，则返回 -1，表示关闭失败
    if (!isOpen()) {
        return -1;
    }

    // 先进行强制缓冲
    endParagraph();

    // 将 allocator 中的数据输出
    size_t result = mParcelBuffer->flush(outBuffer);

    // 释放无用资源操作
    release();

    return result;
}

void TextEncoder::release() {
    if (mParcelBuffer != nullptr) {
        delete mParcelBuffer;
        mParcelBuffer = nullptr;
    }
    // 当前段落信息标记
    if (mCurParagraphPtr != nullptr) {
        delete mCurParagraphPtr;
        mCurParagraphPtr = nullptr;
    }

    if (mParcel != nullptr) {
        delete mParcel;
        mParcel = nullptr;
    }

    mContentTagPtr = nullptr;
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
    // 如果编码器没有 open 处理，或者没有调用 beginParagraph 都抛出异常
    checkEncoderState();

    if (mCurParagraphPtr == nullptr) {
        exit(-1);
    }
}

void TextEncoder::updateText() {
    if (mContentTagPtr != nullptr) {
        mContentTagPtr->writeToParcel(*mParcel);

        delete mContentTagPtr;
        mContentTagPtr = nullptr;
    }
}

void TextEncoder::beginParagraph(TextParagraph::Type paragraphType) {
    // 检测编码器状态
    checkEncoderState();

    // 强制将数据输出到缓冲区
    endParagraph();

    // 创建新的段落
    mCurParagraphPtr = new TextParagraph(paragraphType);
}

void TextEncoder::endParagraph() {
    if (mCurParagraphPtr == nullptr) {
        return;
    }

    // 更新文本信息
    updateText();

    ParagraphTag paragraphTag(mCurParagraphPtr->type);
    // 写入到数据包中
    paragraphTag.writeToParcel(*mParcel);

    // 数据已经存储到缓冲区，释放段落指针
    delete mCurParagraphPtr;
    mCurParagraphPtr = nullptr;

    // 增加段落数
    mCurParagraphCount++;
}

void TextEncoder::addText(const std::vector<std::string> &text) {
    // 检测添加 tag 的环境
    checkTagState();

    // 如果传入的文本信息，为空则返回
    if (text.empty()) {
        return;
    }

    // 添加文本数据
    if (mContentTagPtr == nullptr) {
        mContentTagPtr = new ContentTag(text);
    } else {
        mContentTagPtr->append(text);
    }
}

void TextEncoder::addTextTag(const TextTag &tag) {
    // 检测当前环境
    checkTagState();

    // 更新文本信息
    updateText();

    // 写入到 Parcel 中
    tag.writeToParcel(*mParcel);
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
 *//*

void TextEncoder::addControlTag(TextKind kind, bool isStartTag) {
    // 检测添加 tag 的环境
    checkTagState();

    mContentTagPtr = mParcelBuffer->allocate(4);
    *mContentTagPtr = (char) TextTagType::CONTROL;
    *(mContentTagPtr + 1) = 0;
    *(mContentTagPtr + 2) = (char) kind;
    *(mContentTagPtr + 3) = isStartTag ? 1 : 0;
}

*/
/**
 *
 * @param length：竖直距离
 *
 * 1. tag 类型：占用 1 字节
 * 2. 对齐填充：占用 1 字节
 * 3. 竖直距离：占用 1 字节
 * 4. 对齐填充：占用 1 字节
 *//*

void TextEncoder::addFixedHSpace(unsigned char length) {
    // 检测添加 tag 的环境
    checkTagState();

    mContentTagPtr = mParcelBuffer->allocate(4);
    *mContentTagPtr = (char) TextTagType::FIXED_HSPACE;
    *(mContentTagPtr + 1) = 0;
    *(mContentTagPtr + 2) = length;
    *(mContentTagPtr + 3) = 0;
*/
/*    myParagraphs.back()->addEntry(mContentTagPtr);
    ++myParagraphLengths.back();*//*

}

void TextEncoder::addStyleTag(const StyleTag &tag, unsigned char depth) {
    addStyleTag(tag, tag.fontFamilies(), depth);
}


*/
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
 *//*

void TextEncoder::addStyleTag(const StyleTag &tag, const std::vector<std::string> &fontFamilies,
                              unsigned char depth) {

    // 检测添加 tag 的环境
    checkTagState();

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


    mContentTagPtr = mParcelBuffer->allocate(len);

    char *address = mContentTagPtr;

    *address++ = (char) tag.entryKind();
    *address++ = 0;
    *address++ = depth;
    *address++ = 0;

    address = ParcelBuffer::writeUInt16(address, tag.myFeatureMask);

    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        if (tag.isFeatureSupported((TextFeature) i)) {
            const StyleTag::LengthType &len = tag.myLengths[i];
            address = ParcelBuffer::writeUInt16(address, len.Size);
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
        address = ParcelBuffer::writeUInt16(address,
                */
/*myFontManager.familyListIndex(fontFamilies)*//*
0);
    }

    if (tag.isFeatureSupported(TextFeature::FONT_STYLE_MODIFIER)) {
        *address++ = tag.mySupportedFontModifier;
        *address = tag.myFontModifier;
    }
*/
/*    // --- writing entry

    myParagraphs.back()->addEntry(myLastEntryStart);
    ++myParagraphLengths.back();*//*

}

void TextEncoder::addStyleCloseTag() {
    // 检测添加 tag 的环境
    checkTagState();

    mContentTagPtr = mParcelBuffer->allocate(2);

    char *address = mContentTagPtr;

    *address++ = (char) TextTagType::STYLE_CLOSE;
    *address = 0;

*/
/*    myParagraphs.back()->addEntry(myLastEntryStart);
    ++myParagraphLengths.back();*//*

}

void TextEncoder::addHyperlinkControlTag(TextKind kind, const std::string &label) {

    // 检测添加 tag 的环境
    checkTagState();

    // TODO：占位标记，暂未实现
    mContentTagPtr = mParcelBuffer->allocate(2);

    char *address = mContentTagPtr;

    *address++ = (char) TextTagType::HYPERLINK_CONTROL;
    *address = 0;

*/
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
    ++myParagraphLengths.back();*//*

}


*/
/**
 * 添加图片标签
 * 数据结构：占 4 字节。 | 标签类型 | 边缘对齐 | id
 * 1. tag 类型：占用 1 字节。
 * 2. 边缘对齐：占用 1 字节。
 * 3. 资源 id：占用 2 字节。
 * @param uniqueId：图片指向的资源 id
 * @param tag：图片标签 ==> 这个暂时没啥用，先放着
 *//*

void TextEncoder::addImageTag(uint16_t uniqueId, const ImageTag &tag) {
    // 检测添加 tag 的环境
    checkTagState();

    mContentTagPtr = mParcelBuffer->allocate(4);

    char *address = mContentTagPtr;
    *address++ = (char) TextTagType::IMAGE;
    *address++ = 0;

    // 添加 id 信息
    ParcelBuffer::writeUInt16(address, uniqueId);
}

void TextEncoder::addVideoTag() {
    // 暂时不处理
}*/
