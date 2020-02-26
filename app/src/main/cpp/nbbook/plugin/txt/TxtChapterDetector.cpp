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
static const size_t MIN_CHAPTER_CONTENT_SIZE = 40;

static const std::string TAG = "TxtChapterDetector";

TxtChapterDetector::TxtChapterDetector(const std::string &pattern) {
    mPattern = std::make_shared<Pattern>(pattern);
}

// TODO:detector 最好要返回 true or false
void TxtChapterDetector::detector(const File &file, const std::string &charset,
                                  std::vector<TextChapter> &chapterList) {
    auto inputStreamPtr = file.getInputStream();

    std::string filePath = file.getPath();

    CharsetConverter encodingConverter("utf-8", charset);

    // 将数据解码成 utf-8
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
    int curChapterOriginLen = 0;
    // 源数据中，上一章节的结束位置
    int curChapterOriginEndIndex = 0;
    // block 处理数据的偏移
    int blockOffset = 0;
    // 针对 originBlock 的偏移
    int originBlockOffset = 0;

    // 原理就是将 decode 的数据，慢慢 encoding 获取
    while (!isReader.isFinish()) {
        // todo：存在获取的数据把章节分割的问题，章节一部分内容在 block 的末尾，另一部分在新 block 的开头导致无法匹配的问题。(需要处理)
        // 读取数据
        blockSize = isReader.read(block, BUFFER_SIZE);
        // 创建段落匹配器
        Matcher matcher(mPattern, block, blockSize);

        // 循环查找匹配标题
        while (matcher.find()) {
            // 如果章节列表为空，则创建起始章节。
            if (chapterList.empty()) {
                TextChapter initChapter(filePath, FormatPlugin::CHAPTER_PROLOGUE_TITLE, 0, 0);
                // 加入到列表中
                chapterList.push_back(initChapter);
            }

            // 获取下一章节的标题位置
            size_t nextTitleStartOffset = matcher.start();
            size_t nextTitleEndOffset = matcher.end();
            size_t nextTitleLen = nextTitleEndOffset - nextTitleStartOffset;

            // 获取下一章标题
            std::string nextChapterTitle(block + nextTitleStartOffset, nextTitleLen);

            // 在 block 中当前章片段的起始位置
            char *curChapterStartOffsetFromBlock = block + blockOffset;

            // 当前章节片段内容长度 = 下一章的起始位置 - 当前章的起始位置
            int curChapterLenFromBlock = nextTitleStartOffset - blockOffset;

            // 计算当前章节内容原始大小(在原编码中的长度)
            curChapterOriginLen = getOriginSize(encodingConverter,
                                                curChapterStartOffsetFromBlock,
                                                curChapterLenFromBlock);

            // 当前章节在原编码的起始位置
            curChapterOriginEndIndex = originOffset + originBlockOffset + curChapterOriginLen;

            // 获取真正的当前章
            TextChapter &curChapter = chapterList.back();
            curChapter.endIndex = curChapterOriginEndIndex;

            size_t chapterLen = curChapter.endIndex - curChapter.startIndex;

            // TODO:应该减去章节标题的长度(待处理)
            // 如果当前内容长度，不满足最小章节内容长度
            if (chapterLen < MIN_CHAPTER_CONTENT_SIZE) {
                // 如果是起始章，且章节完全没有内容的时候才处理
                // 或者不是起始章的情况

                // TODO：存在起始内容为空，且第一章匹配到的章节内容为空的情况 (待处理)
                // 如： 第一章    (这样确实有内容了，名字也不是 CHAPTER_PROLOGUE_TITLE 了)
                // 跟着：第二章 xxx。
                // 推荐方案：如果存在这样的情况，新建一个 CHAPTER_PROLOGUE_TITLE 章，内容包裹 第一章
                if (chapterList.size() > 1 ||
                    (chapterList.size() == 1 && chapterLen <= 0)) {
                    chapterList.pop_back();
                }
            }

            // 检测当前仍然有章节
            if (!chapterList.empty()) {
                // 将空章节的内容，赋值给前章节
                TextChapter &realChapter = chapterList.back();
                realChapter.endIndex = curChapterOriginEndIndex;

                // 新章节的起始位置为上一章节的结尾位置
                TextChapter nextChapter(filePath, nextChapterTitle, curChapter.endIndex, 0);
                // 加入到列表中
                chapterList.push_back(nextChapter);
            } else {
                // 如果章节不存在，则 nextChapter 作为起始章节
                TextChapter nextChapter(filePath, nextChapterTitle, 0, 0);
                // 加入到列表中
                chapterList.push_back(nextChapter);
            }

            Logger::i(TAG, curChapter.toString());

            // 源数据块的偏移
            originBlockOffset += curChapterOriginLen;
            // 数据块的偏移
            blockOffset = nextTitleStartOffset;
        }

        // 获取当前处理编码的长度
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


    // TODO:编码操作应该封装一下，封装成 CharDecoder 类(待处理)
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
                // TODO:编码出错，需要抛出异常
                exit(-1);
        }
    }
}