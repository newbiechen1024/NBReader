// author : newbiechen
// date : 2019-10-06 18:59
// description :

#include <util/Logger.h>
#include <reader/textmodel/tag/NBTagStyle.h>
#include <reader/text/entity/TextChapter.h>
#include "TxtReader.h"

static const std::string TAG = "TxtReader";

TxtReader::TxtReader(const PlainTextFormat &format, const std::string &charset)
        : EncodingTextReader(charset), mFormat(format) {

    // 创建核心文本解析器
    if (charset == Charset::UTF16) {
        mReaderCore = std::dynamic_pointer_cast<TxtReaderCore>(
                std::make_shared<TxtReaderCoreUTF16LE>(*this));
    } else if (charset == Charset::UTF16BE) {
        mReaderCore = std::dynamic_pointer_cast<TxtReaderCore>(
                std::make_shared<TxtReaderCoreUTF16BE>(*this));
    } else {
        mReaderCore = std::make_shared<TxtReaderCore>(*this);
    }
}

// todo：不考虑多线程的问题
size_t TxtReader::readContent(TextChapter &chapter, char **outBuffer) {
    size_t chapterSize = chapter.endIndex - chapter.startIndex;

    // 获取 chapter 数据
    char *chapterBuffer = new char[chapterSize];
    File file(chapter.url);

    if (!file.exists()) {
        Logger::e(TAG, "readContent:file not exist " + chapter.url);
        return -1;
    }

    std::shared_ptr<InputStream> inputStream = file.getInputStream();

    if (!inputStream->open()) {
        Logger::e(TAG, "readContent:open inputStream failure");
        return -1;
    }

    // TODO:直接在这里对章节进行解码，不更方便？(再思考思考)
    // 进行随机访问
    inputStream->seek(chapter.startIndex, true);
    // 读取输入流
    inputStream->read(chapterBuffer, chapterSize);
    // 关闭输入流
    inputStream->close();

    // 打开书籍编码器
    mBookEncoder.open();

    // 开始分析
    beginAnalyze();
    // 调用核心类解析类解析文本
    mReaderCore->readContent(chapterBuffer, chapterSize);
    // 结束分析
    endAnalyze();

    // 释放缓存空间
    delete[] chapterBuffer;

    // 关闭书籍编码器
    return mBookEncoder.close(outBuffer);
}

void TxtReader::beginAnalyze() {
    // 标记初始文本为 REGULAR 标准类型
    mBookEncoder.pushTextStyle(TextStyleType::REGULAR);
    // 处理新段落
    // TODO: BookReader 的风格有点像 xml 解析器的风格。beginParagraph 就相当于创建一个 paragraph 标签，之后的操作都是填充标签的内容。
    mBookEncoder.beginParagraph();

    // 初始化参数值
    mConsecutiveEmptyLineCount = 0;
    mCurLineSpaceCount = 0;

    // 默认当前行为空
    isCurLineEmpty = true;
    isNewLine = true;
}

void TxtReader::endAnalyze() {
    endParagraph();
}

bool TxtReader::createNewLine() {
    // 如果当前行不为空
    if (!isCurLineEmpty) {
        mConsecutiveEmptyLineCount = -1;
    }

    // 初始化新行信息
    isCurLineEmpty = true;
    isNewLine = true;
    mCurLineSpaceCount = 0;
    // 默认为空行 ==> 如果不为空就被之前设置为 -1 了
    ++mConsecutiveEmptyLineCount;

    // 是否 format 支持存在一个新行就允许更换段落或者支持存在一个空行才允许更换段落
    // 判断是否支持更换段落
    bool paragraphBreak =
            (mFormat.getBreakType() & PlainTextFormat::BREAK_PARAGRAPH_AT_NEW_LINE) ||
            ((mFormat.getBreakType() & PlainTextFormat::BREAK_PARAGRAPH_AT_EMPTY_LINE) &&
             (mConsecutiveEmptyLineCount > 0));

/*   TODO:不处理标题信息，标题已经自行处理了
     // 如果文本类型包含标题
    if (mFormat.existTitle()) {
        // 如果当前不为 content 段落，并当前连续空行数等于 format 包含了最大连续空行数
        if (!isTitleParagraph &&
            (mConsecutiveEmptyLineCount == mFormat.getEmptyLinesBeforeNewSection())) {
            // 文本段落结束
            endParagraph();

            // 插入片段结束段落标记
            mBookEncoder.insertEndOfSectionParagraph();

            // 启动标题段落
            // TODO:注 beginTitleParagraph 和 beginParagraph 走的是两种逻辑，所以互不冲突
            mBookEncoder.beginTitleParagraph();

            // 标记下一段落的文本类型为 title 类型
            // TODO:注 pushTagStyle 一定要在 beginParagraph()，pushTextStyle 作用是指定下一次 beginParagraph 的类型
            mBookEncoder.pushTextStyle(NBTagStyle::SECTION_TITLE);
            // 启动新的段落
            mBookEncoder.beginParagraph();

            // 标记当前行为 content 类型
            isTitleParagraph = true;
            // 取消更换段落
            paragraphBreak = false;
        }

        // 当为 content paragraph 时，且连续空行为 1
        if (isTitleParagraph && mConsecutiveEmptyLineCount == 1) {
            // 结束标题段落
            mBookEncoder.endTitleParagraph();
            // 删除 SECTION_TITLE 文本样式标记
            mBookEncoder.popTextStyle();

            isTitleParagraph = false;
            paragraphBreak = true;
        }
    }*/

    // 是否允许更换段落
    if (paragraphBreak) {
        endParagraph();
        // 通知开启新段落
        mBookEncoder.beginParagraph();
    }
    return true;
}

void TxtReader::endParagraph() {
    // 重置参数
    if (!isCurLineEmpty) {
        mConsecutiveEmptyLineCount = -1;
    }
    isCurLineEmpty = true;

    mBookEncoder.endParagraph();
}

bool TxtReader::receiveText(std::string &str) {
    const char *ptr = str.data();
    const char *end = ptr + str.length();
    // 统计文本行的空格
    for (; ptr != end; ++ptr) {
        if (std::isspace((unsigned char) *ptr)) {
            if (*ptr != '\t') {
                ++mCurLineSpaceCount;
            } else {
                mCurLineSpaceCount += mFormat.getIgnoredIndent() + 1;
            }
        } else {
            isCurLineEmpty = false;
            break;
        }
    }

    // 如果当前行不为空行
    if (ptr != end) {
        // 如果段落会根据缩进大小换行，并且当前行的缩进大于 Format 提供的缩进
        if ((mFormat.getBreakType() & PlainTextFormat::BREAK_PARAGRAPH_AT_LINE_WITH_INDENT) &&
            isNewLine && (mCurLineSpaceCount > mFormat.getIgnoredIndent())) {
            // 进行换段操作
            endParagraph();
            mBookEncoder.beginParagraph();
        }
        // 将文本添加到 BookReader 中
        mBookEncoder.addText(str);
/*        // 判断当前行是否是标题
        if (isTitleParagraph) {
            mBookEncoder.addTitleText(str);
        }*/

        // 标记当前文本不是新行了
        isNewLine = false;
    }

    return true;
}


TxtReaderCore::TxtReaderCore(TxtReader &reader) : mReader(reader) {
}

size_t TxtReaderCore::readContent(char *inBuffer, size_t bufferSize) {
    std::string str;

    char *startPtr = inBuffer;
    char *endPtr = startPtr + bufferSize;

    // 对每个字节进行处理
    for (char *ptr = startPtr; ptr != endPtr; ++ptr) {
        // 检测到换行符
        if (*ptr == '\n' || *ptr == '\r') {
            bool skipNewLine = false;

            if (*ptr == '\r' && (ptr + 1) != endPtr && *(ptr + 1) == '\n') {
                skipNewLine = true;
                *ptr = '\n';
            }

            // 将获取到的文本段落，交给 Reader 处理
            if (startPtr != ptr) {
                str.erase();
                mReader.convert(str, startPtr, ptr + 1);
                // 处理文本的代码
                mReader.receiveText(str);
            }

            if (skipNewLine) {
                ++ptr;
            }

            startPtr = ptr + 1;
            mReader.createNewLine();
            // 创建一个新行
        } else if (((*ptr) & 0x80) == 0 && std::isspace((unsigned char) *ptr)) {
            if (*ptr != '\t') {
                *ptr = ' ';
            }
        }
    }

    // 如果读取的文本，不包含空格，则直接输出到 text 中。
    if (startPtr != endPtr) {
        str.erase();
        mReader.convert(str, startPtr, endPtr);
        mReader.receiveText(str);
    }
}

TxtReaderCoreUTF16::TxtReaderCoreUTF16(TxtReader &reader) : TxtReaderCore(reader) {
}

size_t TxtReaderCoreUTF16::readContent(char *inBuffer, size_t bufferSize) {
    std::string str;

    char *startPtr = inBuffer;
    char *endPtr = startPtr + bufferSize;

    for (char *ptr = startPtr; ptr < endPtr; ptr += 2) {
        const char chr = getAscii(ptr);
        if (chr == '\n' || chr == '\r') {
            bool skipNewLine = false;
            if (chr == '\r' && ptr + 2 != endPtr && getAscii(ptr + 2) == '\n') {
                skipNewLine = true;
                setAscii(ptr, '\n');
            }
            if (startPtr != ptr) {
                str.erase();
                // 对文字进行编码转换
                mReader.convert(str, startPtr, ptr + 2);

                mReader.receiveText(str);
            }
            if (skipNewLine) {
                ptr += 2;
            }
            startPtr = ptr + 2;
            mReader.createNewLine();
        } else if (chr != 0 && ((*ptr) & 0x80) == 0 && std::isspace(chr)) {
            if (chr != '\t') {
                setAscii(ptr, ' ');
            }
        }
    }
    if (startPtr != endPtr) {
        str.erase();
        mReader.convert(str, startPtr, endPtr);
        mReader.receiveText(str);
    }
}

TxtReaderCoreUTF16LE::TxtReaderCoreUTF16LE(TxtReader &reader) : TxtReaderCoreUTF16(reader) {
}

char TxtReaderCoreUTF16LE::getAscii(const char *ptr) {
    return *(ptr + 1) == '\0' ? *ptr : '\0';
}

void TxtReaderCoreUTF16LE::setAscii(char *ptr, char ascii) {
    *ptr = ascii;
}


TxtReaderCoreUTF16BE::TxtReaderCoreUTF16BE(TxtReader &reader) : TxtReaderCoreUTF16(reader) {
}

char TxtReaderCoreUTF16BE::getAscii(const char *ptr) {
    return *ptr == '\0' ? *(ptr + 1) : '\0';
}

void TxtReaderCoreUTF16BE::setAscii(char *ptr, char ascii) {
    *(ptr + 1) = ascii;
}