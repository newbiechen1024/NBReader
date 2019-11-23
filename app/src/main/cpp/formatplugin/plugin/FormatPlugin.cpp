//
// Created by 陈广祥 on 2019-09-18.
//

#include <tools/language/LangDetector.h>
#include "FormatPlugin.h"
#include "PluginManager.h"
#include <util/Logger.h>

bool FormatPlugin::detectEncodingAndLanguage(Book &book, InputStream &inputStream, bool force) {
    std::string language = book.getLanguage();
    std::string encoding = book.getEncoding();

    if (!force && !encoding.empty()) {
        return true;
    }

    bool detected = false;
    // 如果不知道文本格式，则默认为 UTF-8
    if (encoding.empty()) {
        encoding = Charset::UTF8;
    }
    // 检测文本的 encoding 和语言
    if (inputStream.open()) {
        static const int BUFF_SIZE = 65536;
        char *buffer = new char[BUFF_SIZE];
        const std::size_t size = inputStream.read(buffer, BUFF_SIZE);
        inputStream.close();
        delete[] buffer;

        // 创建探测器，探测语言
        std::shared_ptr<LangDetector::LangInfo> langInfo = LangDetector().findLanguage(buffer,
                                                                                       size);
        if (langInfo != nullptr) {
            detected = true;
            if (!langInfo->lang.empty()) {
                language = langInfo->lang;
            }
            encoding = langInfo->encoding;
            if (encoding == Charset::ASCII || encoding == "iso-8859-1") {
                encoding = "windows-1252";
            }
        }
    }

    book.setEncoding(encoding);
    book.setLanguage(language);

    return detected;
}
