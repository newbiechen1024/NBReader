// author : newbiechen
// date : 2019-10-17 16:08
// description : 语言、文本格式探测器
//

#ifndef NBREADER_LANGDETECTOR_H
#define NBREADER_LANGDETECTOR_H

#include <string>
#include <memory>
#include <vector>
#include <tools/encoding/Charset.h>
#include <map>

class StatisticMatcher;

class LangDetector {
public:
    // 语言信息结构体
    struct LangInfo {
        LangInfo(const std::string &lang, const std::string &encoding);

        const std::string lang;
        const std::string encoding;
    };

    /**
     * 查找文本的编码
     * @param buffer：检测的缓冲区
     * @param length：缓冲区的大小
     * @return
     */
    static std::string findEncoding(const char *buffer, std::size_t length);

public:
    LangDetector();

    ~LangDetector();

    std::shared_ptr<LangInfo> findLanguage(const char *buffer, std::size_t length);

    std::shared_ptr<LangInfo>
    findLanguageWithEncoding(const std::string &encoding, const char *buffer, std::size_t length);

private:
    /**
     * 初始化语言匹配器
     */
    void initLangMatchers();

private:
    // 编码与语言的映射表
    static std::map<std::string, std::string> CHARSET_LANG_MAP;

    typedef std::vector<std::shared_ptr<StatisticMatcher>> MatcherVector;
    // 所有的语言匹配器
    MatcherVector mMatchers;

    bool isInitialize;
};

#endif //NBREADER_LANGDETECTOR_H
