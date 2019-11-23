// author : newbiechen
// date : 2019-11-17 15:04
// description : 文本标签
//

#ifndef NBREADER_TEXTTAG_H
#define NBREADER_TEXTTAG_H


#include <string>
#include <map>

/**
 * 文本基础标签类型
 *
 * 用于 .pgb 文件中
 */
enum class TextBaseTagType : char {
    HEAD = 1,
    PARAGRAPH = 2,
};

/**
 * 文本标签类型
 * 用于 .pgd 文件中
 */
enum class TextTagType : char {
    TEXT = 1, // 文本元素
    IMAGE = 2, // 图片元素
    CONTROL = 3, // 控制元素 (交由上层处理的控制标签，由自己操控)
    HYPERLINK_CONTROL = 4, // 超链接控制元素
    STYLE_CSS = 5,
    STYLE_OTHER = 6,
    STYLE_CLOSE = 7,
    FIXED_HSPACE = 8,
    RESET_BIDI = 9,
    AUDIO = 10,
    VIDEO = 11,
    EXTENSION = 12,
};

/**
 * 本文标签基础类
 */
class TextTag {
protected:
    TextTag() {
    }

public:
    virtual ~TextTag() {
    }

private: // 禁止复制
    TextTag(const TextTag &entry);

    const TextTag &operator=(const TextTag &entry);
};

class TextVideoTag : public TextTag {

public:
    void addSource(const std::string &type, const std::string &path) {
        mSources.insert(std::make_pair(type, path));
    }

    const std::map<std::string, std::string> &getSources() const {
        return mSources;
    }

private:
    std::map<std::string, std::string> mSources;
};

#endif //NBREADER_TEXTTAG_H
