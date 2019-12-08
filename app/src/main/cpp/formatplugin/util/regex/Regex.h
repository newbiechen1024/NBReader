// author : newbiechen
// date : 2019-12-08 11:27
// description : 
//

#ifndef NBREADER_REGEX_H
#define NBREADER_REGEX_H

#include <string>
#include <include/onig/oniguruma.h>

class Matcher;

class Pattern {
public:
    Pattern(const std::string &pattern);

    ~Pattern();

    Matcher match(const char *buffer);

private:
    OnigRegexType *mRegex;

    friend Matcher;
};

class Matcher {
public:
    ~Matcher();

    // 检测是否存在
    bool find();

    /**
     * 匹配到语句的起始位置 offset
     * @return
     */
    size_t start() {
        return mFindStartIndex;
    }

    /**
     * 匹配到语句的终止位置 offset
     * @return
     */
    size_t end() {
        return mFindEndIndex;
    }

private:
    Matcher(Pattern *pattern, const char *buffer);

private:
    // 匹配器
    OnigRegion *mRegion;

    // 匹配数据
    const char *mBuffer;

    // 标记下次匹配数据的起始位置
    const OnigUChar *mStartRange;

    // 查找到的起始索引
    size_t mFindStartIndex;

    // 查找到的终止索引位置
    size_t mFindEndIndex;

    // 是否匹配结束了
    bool isFindFinish;

    Pattern *mPattern;

    friend Pattern;
};

#endif //NBREADER_REGEX_H
