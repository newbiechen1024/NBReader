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

    std::string pattern = "(?-m)^(.{0,8})(\xb5\xda)([0-9\xc1\xe3\xd2\xbb\xb6\xfe\xc1\xbd\xc8\xfd\xcb\xc4\xce\xe5\xc1\xf9\xc6\xdf\xb0\xcb\xbe\xc5\xca\xae\xb0\xd9\xc7\xa7\xcd\xf2\xd2\xbc\xb7\xa1\xc8\xfe\xcb\xc1\xce\xe9\xc2\xbd\xc6\xe2\xb0\xc6\xbe\xc1\xca\xb0\xb0\xdb\xc7\xaa]{1,10})([\xd5\xc2\xbd\xda\xbb\xd8\xbc\xaf\xbe\xed])(.{0,30})$";

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
