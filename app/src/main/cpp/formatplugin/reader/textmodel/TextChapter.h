// author : newbiechen
// date : 2019-12-09 15:50
// description : 
//

#ifndef NBREADER_TEXTCHAPTER_H
#define NBREADER_TEXTCHAPTER_H


#include <string>

class TextChapter {
public:

    TextChapter();

    TextChapter(const std::string &chapterTitle, size_t startIndex, size_t endIndex);

    // 标题
    std::string chapterTitle;
    // 对应源文件的起始位置
    size_t startIndex;
    // 对应源文件的终止位置(指向最后一个字符的下一个位置)
    size_t endIndex;

    std::string toString() {
        std::string str = "chapter:" + chapterTitle + "  startIndex:" + std::to_string(startIndex) +
                          "  endIndex:" + std::to_string(endIndex);
        return str;
    }
};


#endif //NBREADER_TEXTCHAPTER_H
