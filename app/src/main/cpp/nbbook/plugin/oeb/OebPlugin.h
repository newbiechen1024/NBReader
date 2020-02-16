// author : newbiechen
// date : 2020-02-15 09:51
// description : 解析 .oeb .epub 的插件
//

#ifndef NBREADER_OEBPLUGIN_H
#define NBREADER_OEBPLUGIN_H


#include "../FormatPlugin.h"

class OebPlugin : FormatPlugin {
    OebPlugin();

    ~OebPlugin();

    bool readEncodingInternal(std::string &outEncoding) override;

    bool readLanguageInternal(std::string &outLanguage) override;

    bool readChaptersInternal(std::string &chapterPattern,
                              std::vector<TextChapter> &chapterList) override;

    bool
    readChapterContentInternal(TextChapter &txtChapter, char **outBuffer, size_t *outSize) override;
};


#endif //NBREADER_OEBPLUGIN_H
