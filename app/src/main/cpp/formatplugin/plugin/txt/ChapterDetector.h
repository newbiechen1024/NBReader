// author : newbiechen
// date : 2019-12-07 16:47
// description : 章节信息探测器
//

#ifndef NBREADER_CHAPTERDETECTOR_H
#define NBREADER_CHAPTERDETECTOR_H

#include <string>
#include <filesystem/io/InputStream.h>
#include <util/regex/Regex.h>

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
    Pattern mPattern;
};


#endif //NBREADER_CHAPTERDETECTOR_H
