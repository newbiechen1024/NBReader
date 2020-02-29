//
// Created by 陈广祥 on 2019-09-19.
//

#ifndef NBREADER_TXTPLUGIN_H
#define NBREADER_TXTPLUGIN_H


#include "../FormatPlugin.h"
#include "PlainTextFormat.h"
#include "TxtReader.h"

class TxtPlugin : public FormatPlugin {
public:
    TxtPlugin();

    ~TxtPlugin();

    bool readEncodingInternal(std::string &outEncoding) override;

    bool readLanguageInternal(std::string &outLanguage) override;

    bool readChaptersInternal(std::string &chapterPattern,
                              std::vector<TextChapter> &chapterList) override;

    bool
    readChapterContentInternal(TextChapter &inChapter, TextContent &outContent) override;

private:
    // 文本格式
    PlainTextFormat mFormat;
    // 文本阅读器
    TxtReader *mTxtReaderPtr;
};

#endif //NBREADER_TXTPLUGIN_H
