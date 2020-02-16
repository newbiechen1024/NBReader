// author : newbiechen
// date : 2019-09-24 16:22
// description : 
//

#ifndef NBREADER_STRINGUTIL_H
#define NBREADER_STRINGUTIL_H

#include <string>
#include <vector>

class StringUtil {
private:
    StringUtil();

public:
    static bool startsWith(const std::string &str, const std::string &start);

    static bool endsWith(const std::string &str, const std::string &end);

    // 在 string 后添加 num 数字
    static void appendNumber(std::string &str, unsigned int num);

    static void asciiToLowerInline(std::string &asciiString);

    static void stripWhiteSpaces(std::string &str);

    static std::vector<std::string>
    split(const std::string &str, const std::string &delimiter, bool skipEmpty);

    static int parseDecimal(const std::string &str, int defaultValue);

    static double stringToDouble(const std::string &value, double defaultValue);
};


#endif //NBREADER_STRINGUTIL_H
