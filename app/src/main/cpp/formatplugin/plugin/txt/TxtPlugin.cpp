//
// Created by 陈广祥 on 2019-09-19.
//

#include <filesystem/io/FileInputStream.h>
#include <util/Logger.h>
#include <sstream>
#include "TxtPlugin.h"
#include "PlainTextFormat.h"
#include "TxtReader.h"
#include "ChapterDetector.h"


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

    std::string pattern = "^(.{0,8})(\xe7\xac\xac)([0-9\xe9\x9b\xb6\xe4\xb8\x80\xe4\xba\x8c\xe4\xb8\xa4\xe4\xb8\x89\xe5\x9b\x9b\xe4\xba\x94\xe5\x85\xad\xe4\xb8\x83\xe5\x85\xab\xe4\xb9\x9d\xe5\x8d\x81\xe7\x99\xbe\xe5\x8d\x83\xe4\xb8\x87\xe5\xa3\xb9\xe8\xb4\xb0\xe5\x8f\x81\xe8\x82\x86\xe4\xbc\x8d\xe9\x99\x86\xe6\x9f\x92\xe6\x8d\x8c\xe7\x8e\x96\xe6\x8b\xbe\xe4\xbd\xb0\xe4\xbb\x9f]{1,10})([\xe7\xab\xa0\xe8\x8a\x82\xe5\x9b\x9e\xe9\x9b\x86\xe5\x8d\xb7])(.{0,30})$";

    ChapterDetector(pattern).detector(fileInputStream, book.getEncoding());

    Logger::i("TxtPlugin", "readDocument:解析文本结束");
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
