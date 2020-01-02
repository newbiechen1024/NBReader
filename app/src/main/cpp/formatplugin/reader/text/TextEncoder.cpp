// author : newbiechen
// date : 2019-12-30 13:22
// description : 
//

#include <reader/text/tag/TextTagType.h>
#include <util/Logger.h>
#include "TextEncoder.h"

static const size_t BUFFER_SIZE = 8192;

static const std::string TAG = "TextEncoder";

TextEncoder::TextEncoder() : mBufferAllocatorPtr(nullptr),
                             mCurParagraphPtr(nullptr),
                             mCurTagPtr(nullptr) {
    mIsOpen = false;
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
        Logger::i(TAG, "str:" + str);
    }

    UnicodeUtil::Ucs2String unicode2Str;

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
        TextBufferAllocator::writeUInt32(mCurTagPtr + 2, newWordCount);

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
        TextBufferAllocator::writeUInt32(mCurTagPtr + 2, wordCount);
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
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
 * 4. 标签类型：占用 1 字节 ==> 0 或者是
 */
void TextEncoder::addControlTag(TextStyleType style, bool isStartTag) {

    mCurTagPtr = mBufferAllocatorPtr->allocate(4);
    *mCurTagPtr = (char) TextTagType::CONTROL;
    *(mCurTagPtr + 1) = 0;
    *(mCurTagPtr + 2) = (char) style;
    *(mCurTagPtr + 3) = isStartTag ? 1 : 0;
}

