// author : newbiechen
// date : 2019-12-07 16:47
// description : 
//

#include <filesystem/io/InputStreamReader.h>
#include <util/Logger.h>
#include <filesystem/File.h>
#include <filesystem/FileSystem.h>
#include "TxtChapterDetector.h"


// 默认读取 8 字节数据
static const size_t BUFFER_SIZE = 1024 * 8;

static const std::string TAG = "TxtChapterDetector";

TxtChapterDetector::TxtChapterDetector(const std::string &pattern) {
    mPattern = std::make_shared<Pattern>(pattern);
}

void TxtChapterDetector::detector(std::shared_ptr<InputStream> inputStream,
                                  const std::string &charset,
                                  std::vector<TextChapter> &chapterList) {

    // TODO：需要处理编码就是 UTF-8 的情况。
    // TODO：代码写的太垃圾了了需要优化

    // 创建缓冲区
    char *buffer = new char[BUFFER_SIZE]();

    // 输入流读取器
    InputStreamReader isReader(inputStream, charset);

    CharsetConverter encodingConverter("utf-8", charset);

    isReader.open();

    /**
     * 代码分为 3 部分逻辑：
     *
     * 1. 用来解码的数据
     * 2. 解码后的数据
     * 3. 解码后再编码的数据
     */

    // 已解析的长度
    size_t decodeLength = 0;

    // 读取解析完成数据的长度
    int readLen = 0;

    // 编码的起始位置
    int seekOffset = 0;

    int encodingOffset = 0;

    // 还原后的数据长度
    int encodingLen = 0;

    // 最终数据长度
    int chapterEndIndex = 0;

    while (!isReader.isFinish()) {
        // 读取数据
        readLen = isReader.read(buffer, BUFFER_SIZE);

        // 创建段落匹配器
        Matcher matcher(mPattern, buffer, readLen);

        // 循环查找匹配
        while (matcher.find()) {
            size_t findStart = matcher.start();
            size_t findEnd = matcher.end();

            // 获取到匹配的文本
            std::string chapterTitle(buffer + findStart, buffer + findEnd);

            // 计算编码大小
            encodingLen = calculateEncodingSize(encodingConverter, buffer + seekOffset,
                                                findStart - seekOffset);

            if (encodingLen == 0) {
                return;
            }

            chapterEndIndex = decodeLength + encodingOffset + encodingLen;

            encodingOffset += encodingLen;

            seekOffset = findStart;

            // 说明有序章数据，创建序章
            if (chapterList.empty()) {
                if (findStart != 0) {
                    // TODO：可能存在是空格的可能，所以判断如果序章字符太少，直接略过、暂时这么处理
                    TextChapter foreChapter("序章", 0, 0);
                    // 加入到列表中
                    chapterList.push_back(foreChapter);
                } else {
                    TextChapter chapter(chapterTitle, 0, 0);
                    // 加入到列表中
                    chapterList.push_back(chapter);
                }
            }

            // 获取上一章节
            TextChapter &lastChapter = chapterList.back();
            // 新章节的起始位置为上一章节的结尾位置
            lastChapter.endIndex = chapterEndIndex;

            Logger::i(TAG, lastChapter.toString());

            // 创建新章节
            TextChapter newChapter(chapterTitle, lastChapter.endIndex, 0);
            // 加入到列表中
            chapterList.push_back(newChapter);
        }

        // 获取当前 decode 的长度
        decodeLength = isReader.alreadyDecodeLength();
        encodingOffset = 0;
        seekOffset = 0;
    }

    // 如果存在章节
    if (!chapterList.empty()) {
        // 获取最后一个章节，设置其终止索引
        TextChapter &endChapter = chapterList.back();
        endChapter.endIndex = decodeLength;

        Logger::i(TAG, endChapter.toString());
    }
}

// 对传入的数据长度进行编码操作，返回编码操作
int TxtChapterDetector::calculateEncodingSize(CharsetConverter &converter, char *inBuffer,
                                              size_t bufferSize) {
    CharBuffer destBuffer(BUFFER_SIZE);
    CharBuffer sourceBuffer(inBuffer, bufferSize);
    CharsetConverter::ResultCode code;
    int encodingCount = 0;

    // 设置当前已有数据
    sourceBuffer.position(bufferSize);

    for (;;) {
        code = converter.convert(sourceBuffer, destBuffer);
        switch (code) {
            case CharsetConverter::OVERFLOW:
                encodingCount += destBuffer.position();
                destBuffer.clear();
                sourceBuffer.compact();
                break;
            case CharsetConverter::SUCCESS:
                encodingCount += destBuffer.position();
                Logger::i(TAG, "success" + std::to_string(encodingCount));
                return encodingCount;
            default:
                // TODO:抛出异常
                exit(-1);
        }
    }
}