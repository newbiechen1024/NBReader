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

    TextChapter(const std::string &url, const std::string &title,
                size_t startIndex, size_t endIndex);

    // 路径信息
    std::string url;
    // 标题
    std::string title;
    // 对应源文件的起始位置
    int startIndex;
    // 对应源文件的终止位置(指向最后一个字符的下一个位置)
    // TODO：如果传入 -1 表示读取到 url 的最后一个字符(暂时这么设定，为解决 zip 拿不到解压文件的大小问题)
    int endIndex;

    std::string toString() {
        std::string str = "url:" + url + "  chapter:" + title
                          + "  startIndex:" + std::to_string(startIndex)
                          + "  endIndex:" + std::to_string(endIndex);
        return str;
    }
};


#endif //NBREADER_TEXTCHAPTER_H
