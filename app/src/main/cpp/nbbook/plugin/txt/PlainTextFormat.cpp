// author : newbiechen
// date : 2019-09-24 14:12
// description : 
//

#include "PlainTextFormat.h"
#include <stddef.h>

// 缓冲区的大小
static const size_t BUFFER_SIZE = 4096;

// 最大检测长度  1 MB
static const size_t MAX_DETECT_SIZE = 1024 * 1024;

PlainTextFormat::PlainTextFormat() : isInitialized(false),
                                     mBreakType(ParagraphBreakType::BREAK_PARAGRAPH_AT_NEW_LINE),
                                     mIgnoredIndent(1) {
}

void PlainTextDetector::detect(File &file, PlainTextFormat &format) {
    if (!file.exists()) {
        return;
    }
    std::shared_ptr<InputStream> inputStream = file.getInputStream();

    detect(*inputStream, format);
}

void PlainTextDetector::detect(InputStream &inputStream, PlainTextFormat &format) {
    // 如果文件流未开启，则返回
    if (!inputStream.open()) {
        return;
    }

    // 表格的大小
    const unsigned int tableSize = 10;

    unsigned int lineCounter = 0;

    size_t curDetectSize = 0;

    int emptyLineCounter = -1;
    // 记录文本数大于 81 字节的行
    unsigned int stringsWithLengthLessThan81Counter = 0;
    // 记录文本的缩进
    unsigned int stringIndentTable[tableSize] = {0};

    bool currentLineIsEmpty = true;
    unsigned int currentLineLength = 0;
    unsigned int currentLineIndent = 0;

    char *buffer = new char[BUFFER_SIZE]();
    int length;

    do {
        // 获取数据写入到缓冲区
        length = inputStream.read(buffer, BUFFER_SIZE);
        // 缓冲区的结束位置
        const char *end = buffer + length;
        // 循环 char
        for (const char *ptr = buffer; ptr != end; ++ptr) {
            // 当前行的长度
            ++currentLineLength;
            // 如果为换行符
            if (*ptr == '\n') {
                // 行数量 + 1
                ++lineCounter;
                // 如果当前行是空行
                if (currentLineIsEmpty) {
                    // 设置空行的总数 +1
                    ++emptyLineCounter;
                }

                // 统计字节数小于 81 的行
                if (currentLineLength < 81) {
                    ++stringsWithLengthLessThan81Counter;
                }
                // 如果当前是非空行
                if (!currentLineIsEmpty) {
                    // 记录文本缩进
                    stringIndentTable[std::min(currentLineIndent, tableSize - 1)]++;
                }
                // 重置变量
                currentLineIsEmpty = true;
                currentLineLength = 0;
                currentLineIndent = 0;
            } else if (*ptr == '\r') { // 无视制表符
                continue;
            } else if (std::isspace((unsigned char) *ptr)) { // 如果是空格
                if (currentLineIsEmpty) { // 如果当前行是空行，则缩进记录 + 1
                    ++currentLineIndent;
                }
            } else {
                currentLineIsEmpty = false;
            }
        }
        curDetectSize += length;
    } while (length == BUFFER_SIZE && curDetectSize < MAX_DETECT_SIZE); // 循环直到文本结尾

    // 上面的代码遍历文本，统计了以下信息
    // 1. 1-10 的缩进数量
    // 2. 1-10 在非空行前，空行的数量。
    // 3. 1-10 在存在空行前，记录小于 51 字节的行的数量。
    // 4. 记录大于 81 字节的行的数量
    // 5. 行的总数

    // 释放缓冲区
    delete[] buffer;
    // 非空行的数量
    unsigned int nonEmptyLineCounter = lineCounter - emptyLineCounter;

    // 也就是说，如果缩进的行数太多了，则之后的缩进无效。
    {
        // 缩进的距离
        unsigned int indent = 0;
        // 缩进的行数
        unsigned int lineWithIndent = 0;
        for (; indent < tableSize; ++indent) {
            // 根据缩进的距离获取缩进的行数
            lineWithIndent += stringIndentTable[indent];
            // 如果缩进的行数 > 0.1 倍的未缩进行数
            if (lineWithIndent > 0.1 * nonEmptyLineCounter) {
                break;
            }
        }
        // 设置无视缩进的距离
        format.mIgnoredIndent = (indent + 1);
    }

    // 根据文本的长短，决定使用的 breakType
    {
        int breakType = 0;
        breakType |= PlainTextFormat::BREAK_PARAGRAPH_AT_EMPTY_LINE;
        if (stringsWithLengthLessThan81Counter < 0.3 * nonEmptyLineCounter) {
            breakType |= PlainTextFormat::BREAK_PARAGRAPH_AT_NEW_LINE;
        } else {
            breakType |= PlainTextFormat::BREAK_PARAGRAPH_AT_LINE_WITH_INDENT;
        }
        format.mBreakType = (breakType);
    }

    // 设置初始化成功
    format.isInitialized = true;
}