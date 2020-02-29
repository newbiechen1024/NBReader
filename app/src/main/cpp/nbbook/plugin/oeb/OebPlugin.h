// author : newbiechen
// date : 2020-02-15 09:51
// description : 解析 .oeb .epub 的插件
//

#ifndef NBREADER_OEBPLUGIN_H
#define NBREADER_OEBPLUGIN_H


#include "../FormatPlugin.h"
#include "OpfReader.h"
#include "OebReader.h"
#include <string>

class OebPlugin : public FormatPlugin {

private:
    /**
     * 从文件中查找并获取 opf 文件
     * @param oebFile
     * @return
     */
    static File findOpfFile(const File &oebFile);

    /**
     * 从文件中查找并获取 epub 文件
     * @param oebFile
     * @return
     */
    static File findEpubFile(const File &oebFile);

public:
    OebPlugin();

    ~OebPlugin();

protected:
    void onInit() override;

    bool readEncodingInternal(std::string &outEncoding) override;

    bool readLanguageInternal(std::string &outLanguage) override;

    bool readChaptersInternal(std::string &chapterPattern,
                              std::vector<TextChapter> &chapterList) override;

    bool
    readChapterContentInternal(TextChapter &inChapter, TextContent &outContent) override;

private:
    OpfReader mOpfReader;
    OebReader mOebReader;

};


#endif //NBREADER_OEBPLUGIN_H
