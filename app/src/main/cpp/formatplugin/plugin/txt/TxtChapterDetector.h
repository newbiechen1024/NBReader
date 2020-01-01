// author : newbiechen
// date : 2019-12-07 16:47
// description : 纯文本章节探测器
//

#ifndef NBREADER_TXTCHAPTERDETECTOR_H
#define NBREADER_TXTCHAPTERDETECTOR_H

#include <string>
#include <filesystem/io/InputStream.h>
#include <util/regex/Regex.h>
#include <filesystem/charset/CharsetConverter.h>
#include <reader/text/entity/TextChapter.h>

class TxtChapterDetector {
public:
    /**
     *
     * @param pattern 章节匹配正则
     * @param charset
     */
    TxtChapterDetector(const std::string &pattern);

    /**
     * TODO：传入参数不太好，需要调整
     * @param inputStream
     * @param charset
     * @param chapterList
     */
    void detector(const File &file, const std::string &charset, std::vector<TextChapter> &chapterList);

private:
    /**
     * 将 utf-8 编码大小转换为原始大小
     * @param converter
     * @param inBuffer
     * @param bufferSize
     * @return
     */
    int getOriginSize(CharsetConverter &converter, char *inBuffer, size_t bufferSize);

private:
    std::shared_ptr<Pattern> mPattern;
};


#endif //NBREADER_TXTCHAPTERDETECTOR_H
