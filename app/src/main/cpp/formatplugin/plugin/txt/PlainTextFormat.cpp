// author : newbiechen
// date : 2019-09-24 14:12
// description : 
//

#include "PlainTextFormat.h"
#include <stddef.h>

// 缓冲区的大小
const size_t BUFFER_SIZE = 4096;

PlainTextFormat::PlainTextFormat(const File &file)
        : isInitialized(false),
          mBreakType(ParagraphBreakType::BREAK_PARAGRAPH_AT_NEW_LINE),
          mIgnoredIndent(1),
          mEmptyLinesBeforeNewSection(1),
          isExistTitle(false) {
}

void PlainTextDetector::detect(InputStream &inputStream, PlainTextFormat &format) {
    // 如果文件流未开启，则返回
    if (!inputStream.open()) {
        return;
    }

    // 表格的大小
    const unsigned int tableSize = 10;

    unsigned int lineCounter = 0;

    int emptyLineCounter = -1;
    // 记录文本数大于 81 字节的行
    unsigned int stringsWithLengthLessThan81Counter = 0;
    // 记录文本的缩进
    unsigned int stringIndentTable[tableSize] = {0};
    // 记录空行的数组
    unsigned int emptyLinesTable[tableSize] = {0};
    // 记录一行小于 51 字节的行
    unsigned int emptyLinesBeforeShortStringTable[tableSize] = {0};

    bool currentLineIsEmpty = true;
    unsigned int currentLineLength = 0;
    unsigned int currentLineIndent = 0;
    int currentNumberOfEmptyLines = -1;

    char *buffer = new char[BUFFER_SIZE];
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
                    // 在非空行前的空行数
                    ++currentNumberOfEmptyLines;
                } else {
                    // 在非空行前存在空行
                    if (currentNumberOfEmptyLines >= 0) {
                        // 根据空行的数量，决定索引。如果空行超出了 tableSize 则索引为 tableSize
                        int index = std::min(currentNumberOfEmptyLines, (int) tableSize - 1);
                        // 设置索引位置 +1
                        emptyLinesTable[index]++;
                        // 如果当前行的数量小于 51 个字节
                        if (currentLineLength < 51) {
                            // 设置在短文本之前的空行数 + 1
                            emptyLinesBeforeShortStringTable[index]++;
                        }
                    }
                    // 重置空行
                    currentNumberOfEmptyLines = -1;
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
    } while (length == BUFFER_SIZE); // 循环直到文本结尾

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

    // 记录，文本中的段落一般间隔多少行
    {
        // 最多的空行值
        unsigned int max = 0;

        unsigned index;
        // 到达非空行的时，最大的空行值
        int emptyLinesBeforeNewSection = -1;
        for (index = 2; index < tableSize; ++index) {
            if (max < emptyLinesBeforeShortStringTable[index]) {
                max = emptyLinesBeforeShortStringTable[index];
                // 最大的连续空行
                emptyLinesBeforeNewSection = index;
            }
        }
        // 如果最大空行索引 > 0
        if (emptyLinesBeforeNewSection > 0) {
            // 将值向前相加。
            for (index = tableSize - 1; index > 0; --index) {
                emptyLinesTable[index - 1] += emptyLinesTable[index];
                emptyLinesBeforeShortStringTable[index -
                                                 1] += emptyLinesBeforeShortStringTable[index];
            }
            //
            for (index = emptyLinesBeforeNewSection; index < tableSize; ++index) {
                // 如果 x 空行后遇到短行的次数大于 70%
                if ((emptyLinesBeforeShortStringTable[index] > 2) &&
                    (emptyLinesBeforeShortStringTable[index] > 0.7 * emptyLinesTable[index])) {
                    break;
                }
            }
            // 如果超出 10 就不管，如果未超出 10 则以 index 为准
            emptyLinesBeforeNewSection = (index == tableSize) ? -1 : (int) index;
        }

        // TODO:这是用来之后判断是否是标题的依据，FBReader 判断标题的方式太简单了，必须保证别人的 txt 是按照标准来走的，否则就会出问题。不适合中文判断，以后需要改进。

        // 设置新片段前的空行数
        format.mEmptyLinesBeforeNewSection = emptyLinesBeforeNewSection;
        format.isExistTitle = (emptyLinesBeforeNewSection > 0);
    }

    // 设置初始化成功
    format.isInitialized = true;
}