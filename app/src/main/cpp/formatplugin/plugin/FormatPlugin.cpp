//
// Created by 陈广祥 on 2019-09-18.
//

#include <tools/language/LangDetector.h>
#include "FormatPlugin.h"
#include "PluginManager.h"

bool FormatPlugin::detectEncodingAndLanguage(Book &book, InputStream &inputStream, bool force) {
    std::string language = book.getLanguage();
    Charset encoding = book.getEncoding();

    if (!force && (encoding != Charset::NONE)) {
        return true;
    }

    bool detected = false;
    PluginManager &pluginManager = PluginManager::getInstance();
    // 如果不知道文本格式，则默认为 UTF-8
    if (encoding == Charset::NONE) {
        encoding = Charset::UTF8;
    }
    // 检测文本的 encoding 和语言
    if (inputStream.open()) {
        static const int BUFF_SIZE = 65536;
        char *buffer = new char[BUFF_SIZE];
        const std::size_t size = inputStream.read(buffer, BUFF_SIZE);
        inputStream.close();
        // 如何获取
        std::shared_ptr<LangDetector::LangInfo> langInfo = LangDetector().findLanguage(buffer, size);
        delete[] buffer;
        if (langInfo == nullptr) {
            detected = true;
            if (!langInfo->lang.empty()) {
                language = langInfo->lang;
            }
            encoding = langInfo->encoding;
            if (encoding == Charset::ASCII || encoding == Charset::ISO8859) {
                encoding = Charset::WINDOWS1252;
            }
        }
    }

    book.setEncoding(encoding);
    book.setLanguage(language);

    return detected;
}
