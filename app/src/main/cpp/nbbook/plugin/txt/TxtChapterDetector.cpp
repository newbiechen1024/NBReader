// author : newbiechen
// date : 2019-12-07 16:47
// description : 
//

#include "TxtChapterDetector.h"
#include "../../filesystem/io/InputStreamReader.h"
#include "../FormatPlugin.h"
#include "../../util/Logger.h"


// 默认读取 8 字节数据
static const size_t BUFFER_SIZE = 1024 * 8;

static const std::string TAG = "TxtChapterDetector";

TxtChapterDetector::TxtChapterDetector(const std::string &pattern) {
    mPattern = std::make_shared<Pattern>(pattern);
}

// TODO:detector 最好要返回 true or false
void TxtChapterDetector::detector(const File &file, const std::string &charset,
                                  std::vector<TextChapter> &chapterList) {

    // TODO：需要处理编码就是 UTF-8 的情况。
    // TODO：代码写的太垃圾了了需要优化

    auto inputStreamPtr = file.getInputStream();

    std::string filePath = file.getPath();

    CharsetConverter encodingConverter("utf-8", charset);

    // 读取解码后的数据
    InputStreamReader isReader(inputStreamPtr, charset);

    if (!isReader.open()) {
        return;
    }

    /**
     * 代码分为 3 部分逻辑：
     *
     * 1. 用来解码的数据
     * 2. 解码后的数据
     * 3. 解码后再编码的数据
     */

    // 创建解码后数据的大小
    char *block = new char[BUFFER_SIZE]();
    // 缓冲块的大小
    int blockSize = 0;
    // 源数据的起始偏移位置
    int originOffset = 0;
    // 源章节大小
    int originChapterLen = 0;
    // 源数据中，上一章节的结束位置
    int originLastChapterEndIndex = 0;
    // block 处理数据的偏移
    int blockOffset = 0;
    // 针对 originBlock 的偏移
    int originBlockOffset = 0;

    // 原理就是将 decode 的数据，慢慢 encoding 获取
    while (!isReader.isFinish()) {
        // 读取数据
        blockSize = isReader.read(block, BUFFER_SIZE);
        // 创建段落匹配器
        Matcher matcher(mPattern, block, blockSize);

        // 循环查找匹配
        while (matcher.find()) {
            size_t findStart = matcher.start();
            size_t findEnd = matcher.end();

            // 获取到匹配的文本
            std::string chapterTitle(block + findStart, block + findEnd);

            char *chapterStartOffset = block + blockOffset;
            int chapterSize = findStart - blockOffset;

            // 计算章节大小
            originChapterLen = getOriginSize(encodingConverter,
                                             chapterStartOffset,
                                             chapterSize);

            // TODO:如果计算失败，则直接返回(需要错误处理)
            if (originChapterLen == 0) {
                return;
            }

            // 当前章节在源数据的起始位置
            originLastChapterEndIndex = originOffset + originBlockOffset + originChapterLen;

            // 源数据块的偏移
            originBlockOffset += originChapterLen;

            // 数据块的偏移
            blockOffset = findStart;

            // 说明有序章数据，创建序章
            if (chapterList.empty()) {
                if (findStart != 0) {
                    // TODO：可能存在是空格的可能，所以判断如果序章字符太少，直接略过 (还未实现)
                    TextChapter initChapter(filePath, FormatPlugin::CHAPTER_PROLOGUE_TITLE, 0, 0);
                    // 加入到列表中
                    chapterList.push_back(initChapter);
                } else {
                    TextChapter chapter(filePath, chapterTitle, 0, 0);
                    // 加入到列表中
                    chapterList.push_back(chapter);
                }
            }

            // 获取上一章节
            TextChapter &lastChapter = chapterList.back();
            lastChapter.endIndex = originLastChapterEndIndex;

            Logger::i(TAG, lastChapter.toString());

            // 新章节的起始位置为上一章节的结尾位置
            TextChapter newChapter(filePath, chapterTitle, lastChapter.endIndex, 0);
            // 加入到列表中
            chapterList.push_back(newChapter);
        }

        // 获取当前 decode 的长度
        originOffset = isReader.alreadyDecodeLength();
        originBlockOffset = 0;
        blockOffset = 0;
    }

    // 如果存在章节
    if (!chapterList.empty()) {
        // 获取最后一个章节，设置其终止索引
        TextChapter &endChapter = chapterList.back();
        endChapter.endIndex = originOffset;

        Logger::i(TAG, endChapter.toString());
    }
    delete[] block;
}

// 对传入的数据长度进行编码操作，返回编码操作
int TxtChapterDetector::getOriginSize(CharsetConverter &converter, char *inBuffer,
                                      size_t bufferSize) {

    // 如果是相同编码，直接返回长度
    if (converter.getFromEncoding() == converter.getToEncoding()) {
        return bufferSize;
    }

    CharBuffer destBuffer(BUFFER_SIZE);
    CharBuffer sourceBuffer(inBuffer, bufferSize, bufferSize);
    CharsetConverter::ResultCode code;
    int encodingCount = 0;

    for (;;) {
        code = converter.convert(sourceBuffer, destBuffer);
        switch (code) {
            case CharsetConverter::OVERFLOW: {
                encodingCount += destBuffer.position();
                destBuffer.clear();

                // 重新定位需要为处理的 buffer
                char *newStart = sourceBuffer.buffer() + sourceBuffer.position();
                size_t newSize = sourceBuffer.remaining();
                sourceBuffer = CharBuffer(newStart, newSize, newSize);
                break;
            }
            case CharsetConverter::SUCCESS:
                encodingCount += destBuffer.position();
                return encodingCount;
            default:
                // TODO:抛出异常
                exit(-1);
        }
    }
}