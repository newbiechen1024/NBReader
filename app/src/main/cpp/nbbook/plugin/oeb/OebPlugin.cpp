// author : newbiechen
// date : 2020-02-15 09:51
// description : 
//

#include "OebPlugin.h"

bool OebPlugin::readEncodingInternal(std::string &outEncoding) {
    // 读取编码信息
    return false;
}

bool OebPlugin::readLanguageInternal(std::string &outLanguage) {
    // 读取语言信息
    return false;
}

bool OebPlugin::readChaptersInternal(std::string &chapterPattern,
                                     std::vector<TextChapter> &chapterList) {
    // 读取章节信息
    return false;
}

bool OebPlugin::readChapterContentInternal(TextChapter &txtChapter, char **outBuffer,
                                           size_t *outSize) {
    return false;
}