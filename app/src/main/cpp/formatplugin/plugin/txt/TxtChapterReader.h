// author : newbiechen
// date : 2019-11-25 23:27
// description : 
//

#ifndef NBREADER_TXTCHAPTERREADER_H
#define NBREADER_TXTCHAPTERREADER_H


#include "../EncodingTextReader.h"
#include <string>
#include <filesystem/io/InputStream.h>
#include <regex.h>

class TxtChapterReaderCore;

class TxtChapterReader : EncodingTextReader {
public:
    TxtChapterReader(const std::string &lang, const std::string &charset);

    ~TxtChapterReader();
    void readDocument(InputStream &inputStream);

private:
    void initRegexStr(const std::string &lang, const std::string &charset);

private:
    // 正则模式匹配对象
    regex_t mRegex;
    // 错误码
    int mRegexCode;
};

#endif //NBREADER_TXTCHAPTERREADER_H
