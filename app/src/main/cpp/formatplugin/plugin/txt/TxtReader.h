// author : newbiechen
// date : 2019-10-06 18:59
// description : txt 文本读取解析器
//

#ifndef NBREADER_TXTREADER_H
#define NBREADER_TXTREADER_H


#include <plugin/EncodingTextReader.h>
#include <filesystem/io/InputStream.h>
#include "PlainTextFormat.h"
#include <memory>
#include <reader/bookmodel/BookModel.h>
#include <reader/bookmodel/BookReader.h>

class TextReaderCore;

class TxtReader : EncodingTextReader {

protected:
    TxtReader(BookModel &model, const PlainTextFormat &format, Charset charset);

    ~TxtReader() {
    }

    // 启动文稿分析
    void startDocument();

    // 进行文稿分析
    void readDocument();

    // 结束文稿分析
    void endDocument();

    // 接收文本数据
    bool receiveData(std::string &str);

    // 创建新行
    bool createNewLine();

private:
    // 文本信息
    const PlainTextFormat &mFormat;
    // 核心文本解析器
    std::shared_ptr<TextReaderCore> mReaderCore;
    // 书籍内容处理器
    BookReader mBookReader;

    void endParagraphInternal();

    friend class TextReaderCore;

    friend class TxtReaderCoreUTF16;

    friend class TxtReaderCoreUTF16BE;
};

// 文本核心处理器
class TxtReaderCore {
public:
    TxtReaderCore(TxtReader &reader);

    virtual ~TextReaderCore() {
    }

    virtual void readDocument(InputStream &stream);

protected:
    TxtReader &mReader;
};

class TxtReaderCoreUTF16 {

};

class TxtReaderCoreUTF16BE {

};

#endif //NBREADER_TXTREADER_H
