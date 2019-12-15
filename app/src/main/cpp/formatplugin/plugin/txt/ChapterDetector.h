// author : newbiechen
// date : 2019-12-07 16:47
// description : 章节信息探测器
//

#ifndef NBREADER_CHAPTERDETECTOR_H
#define NBREADER_CHAPTERDETECTOR_H

#include <string>
#include <filesystem/io/InputStream.h>
#include <util/regex/Regex.h>
#include <reader/textmodel/TextChapter.h>
#include <filesystem/charset/CharsetConverter.h>

class ChapterDetector {
public:
    /**
     *
     * @param pattern 章节匹配正则
     * @param charset
     */
    ChapterDetector(const std::string &pattern);

    void detector(std::shared_ptr<InputStream> inputStream, const std::string &charset);

private:
    int encodingSize(CharsetConverter & converter,char *inBuffer,size_t bufferSize);

private:
    std::shared_ptr<Pattern> mPattern;
    std::vector<TextChapter> mChapterList;
};


#endif //NBREADER_CHAPTERDETECTOR_H
