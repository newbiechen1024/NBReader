//
// Created by 陈广祥 on 2019-09-19.
//

#include <filesystem/io/FileInputStream.h>
#include "TxtPlugin.h"
#include "PlainTextFormat.h"
#include "TxtReader.h"

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
    // 创建文本参数信息
    PlainTextFormat format(file);
    // 如果参数信息未初始化
    if (!format.hasInitialized()) {
        // 调用探测器进行探测
        PlainTextDetector detector;
        detector.detect(*fileInputStream, format);
    }
    // 读取文本的语言和编码信息
    readLanguageAndEncoding(book);
    // 创建文本阅读器
    TxtReader(bookModel, format, book.getEncoding()).readDocument(*fileInputStream);
    return true;
}

bool TxtPlugin::readLanguageAndEncoding(Book &book) const {
    std::shared_ptr<InputStream> stream = book.getFile().getInputStream();
    if (stream == nullptr) {
        return false;
    }

    detectEncodingAndLanguage(book, *stream);
    return book.getEncoding() == Charset::NONE;
}

const FormatType TxtPlugin::supportType() const {
    return TXT;
}
