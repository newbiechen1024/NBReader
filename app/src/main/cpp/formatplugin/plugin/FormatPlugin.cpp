//
// Created by 陈广祥 on 2019-09-18.
//

#include "FormatPlugin.h"
#include "PluginManager.h"

bool FormatPlugin::detectEncodingAndLanguage(Book &book, InputStream &inputStream, bool force) {
    std::string language = book.getLanguage();
    std::string encoding = book.getEncoding();

    if (!force && !encoding.empty()) {
        return true;
    }

    bool detected = false;
    PluginManager &pluginManager = PluginManager::getInstance();

    // TODO:编码处理 ==> 设计到一套编码的管理，之后做

  /*  // 如果不知道文本格式，则默认为 UTF-8
    if (encoding.empty()) {
        encoding = ZLEncodingConverter::UTF8;
    }
    // 检测文本的 encoding 和语言
    if (collection.isLanguageAutoDetectEnabled() && inputStream.open()) {
        static const int BUFSIZE = 65536;
        char *buffer = new char[BUFSIZE];
        const std::size_t size = inputStream.read(buffer, BUFSIZE);
        inputStream.close();
        shared_ptr<ZLLanguageDetector::LanguageInfo> info = ZLLanguageDetector().findInfo(buffer,
                                                                                          size);
        delete[] buffer;
        if (!info.isNull()) {
            detected = true;
            if (!info->Language.empty()) {
                language = info->Language;
            }
            encoding = info->Encoding;
            if (encoding == ZLEncodingConverter::ASCII || encoding == "iso-8859-1") {
                encoding = "windows-1252";
            }
        }
    }*/

    book.setEncoding(encoding);
    book.setLanguage(language);

    return detected;
}
