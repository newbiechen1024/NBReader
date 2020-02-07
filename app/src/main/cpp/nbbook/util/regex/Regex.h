// author : newbiechen
// date : 2019-12-08 11:27
// description : 
//

#ifndef NBREADER_REGEX_H
#define NBREADER_REGEX_H

#include <string>
#include <onig/oniguruma.h>

class Matcher;

class Pattern {
public:
    Pattern(const std::string &pattern);

    ~Pattern();

private:
    OnigRegexType *mRegex;

    friend Matcher;
};

class Matcher {
public:
    Matcher(std::shared_ptr<Pattern> pattern, const char *buffer, size_t bufferLen);

    ~Matcher();

    // 检测是否存在
    bool find();

    /**
     * 匹配到语句的起始位置
     * 如 "test" 匹配 "test",完全匹配其 start 为 0
     * @return
     */
    size_t start() {
        return mFindStartIndex;
    }

    /**
     * 匹配到语句的结尾位置
     * 如 "test" 匹配 "test",完全匹配其 end 为 4。即指向 t 字符的后一位置，表示终结。
     * @return
     */
    size_t end() {
        return mFindEndIndex;
    }

private:
    // 匹配器
    OnigRegion *mRegion;

    // 匹配数据
    const char *mBuffer;

    size_t mBufferLen;

    // 标记下次匹配数据的起始位置
    const OnigUChar *mStartRange;

    // 查找到的起始索引
    size_t mFindStartIndex;

    // 查找到的终止索引位置
    size_t mFindEndIndex;

    // 是否匹配结束了
    bool isFindFinish;

    std::shared_ptr<Pattern> mPattern;
};

#endif //NBREADER_REGEX_H
