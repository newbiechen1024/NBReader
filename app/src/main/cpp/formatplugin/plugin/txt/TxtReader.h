// author : newbiechen
// date : 2019-10-06 18:59
// description : txt 文本读取解析器
// 注：FBReader 中 Line 和 Paragraph 代表两种类型。如，当 ParagraphBreakType == BREAK_PARAGRAPH_AT_NEW_LINE 则 Line == Paragraph

#ifndef NBREADER_TXTREADER_H
#define NBREADER_TXTREADER_H


#include <plugin/EncodingTextReader.h>
#include <filesystem/io/InputStream.h>
#include "PlainTextFormat.h"
#include <memory>
#include <reader/bookmodel/BookModel.h>
#include <reader/bookmodel/BookReader.h>

class TxtReaderCore;

class TxtReader : public EncodingTextReader {

public:
    TxtReader(BookModel &model, const PlainTextFormat &format, const std::string &charset);

    ~TxtReader() {
    }

    // 进行文稿分析
    void readDocument(InputStream &stream);

private:
    // 文本信息
    const PlainTextFormat &mFormat;
    // 核心文本解析器
    std::shared_ptr<TxtReaderCore> mReaderCore;
    // 书籍内容处理器
    BookReader mBookReader;

    // 当前是否是新行
    bool isNewLine;
    // 当前段落是否是标题段落
    bool isTitleParagraph;
    // 当前行是否为空值
    bool isCurLineEmpty;
    // 统计一行的空格数
    int mCurLineSpaceCount;
    // 当前行之前的连续空行数
    int mConsecutiveEmptyLineCount;

    // 启动文稿分析
    void beginAnalyze();

    // 结束文稿分析
    void endAnalyze();

    // 接受一段文本数据
    bool receiveText(std::string &str);

    // 创建新行 ==> 不等同于创建新段落
    bool createNewLine();

    void endParagraph();

    friend class TxtReaderCore;

    friend class TxtReaderCoreUTF16;

    friend class TxtReaderCoreUTF16LE;

    friend class TxtReaderCoreUTF16BE;
};

// 文本核心处理器
class TxtReaderCore {
public:
    TxtReaderCore(TxtReader &reader);

    virtual ~TxtReaderCore() {
    }

    virtual void readDocument(InputStream &stream);

protected:
    TxtReader &mReader;
};

class TxtReaderCoreUTF16 : public TxtReaderCore {
public:
    TxtReaderCoreUTF16(TxtReader &reader);

    virtual ~TxtReaderCoreUTF16() {
    };

    void readDocument(InputStream &stream) override;

protected:
    virtual char getAscii(const char *ptr) = 0;

    virtual void setAscii(char *ptr, char ascii) = 0;
};


class TxtReaderCoreUTF16LE : public TxtReaderCoreUTF16 {
public:
    TxtReaderCoreUTF16LE(TxtReader &reader);

    ~TxtReaderCoreUTF16LE() {
    };

protected:
    char getAscii(const char *ptr) override;

    void setAscii(char *ptr, char ascii) override;
};

class TxtReaderCoreUTF16BE : public TxtReaderCoreUTF16 {
public:
    TxtReaderCoreUTF16BE(TxtReader &reader);

    ~TxtReaderCoreUTF16BE() {
    };
protected:
    char getAscii(const char *ptr) override;

    void setAscii(char *ptr, char ascii) override;
};

#endif //NBREADER_TXTREADER_H
