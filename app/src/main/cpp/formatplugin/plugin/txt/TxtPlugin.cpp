//
// Created by 陈广祥 on 2019-09-19.
//

#include <filesystem/io/FileInputStream.h>
#include <util/Logger.h>
#include <sstream>
#include "TxtPlugin.h"
#include "PlainTextFormat.h"
#include "TxtReader.h"
#include "TxtChapterReader.h"
#include <re2.h>


TxtPlugin::TxtPlugin() {
}

TxtPlugin::~TxtPlugin() {
}

bool TxtPlugin::readMetaInfo(Book &book) const {
    return true;
}

bool TxtPlugin::readModel(BookModel &bookModel) const {
    Book &book = *bookModel.getBook();
    // 获取 Book 对应的 File
    const File &file = book.getFile();
    // 获取 InputStream 输入流
    std::shared_ptr<InputStream> fileInputStream = file.getInputStream();

/*    // 创建文本参数信息
    PlainTextFormat format(file);

    // TODO：如果改成 chapter 解析，下面几种都可以根据章节来分析了，没必要遍历整本 book

    // 如果参数信息未初始化
    if (!format.hasInitialized()) {
        // 调用探测器进行探测
        PlainTextDetector detector;
        detector.detect(*fileInputStream, format);
    }*/
    Logger::i("TxtPlugin", "readLanguageAndEncoding:开始");
    // 读取文本的语言和编码信息
    readLanguageAndEncoding(book);

    Logger::i("TxtPlugin",
              "readLanguageAndEncoding: lang = " + book.getLanguage() + "   encoding = " +
              book.getEncoding());

    Logger::i("TxtPlugin", "readDocument:解析开始");

/*    // 创建文本阅读器
    TxtReader(bookModel, format, book.getEncoding()).readDocument(*fileInputStream);*/

/*    TxtChapterReader(book.getLanguage(), book.getEncoding()).readDocument(*fileInputStream);
    Logger::i("TxtPlugin", "readDocument:解析文本结束");*/
    return true;
}

bool TxtPlugin::readLanguageAndEncoding(Book &book) const {
    std::shared_ptr<InputStream> stream = book.getFile().getInputStream();
    if (stream == nullptr) {
        return false;
    }

    detectEncodingAndLanguage(book, *stream);
    return !book.getEncoding().empty();
}

const FormatType TxtPlugin::supportType() const {
    return FormatType::TXT;
}
