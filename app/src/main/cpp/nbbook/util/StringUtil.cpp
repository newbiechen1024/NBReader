// author : newbiechen
// date : 2019-09-24 16:22
// description : 
//

#include "StringUtil.h"

bool StringUtil::startsWith(const std::string &str, const std::string &start) {
    return
            (start.length() <= str.length()) &&
            #if __GNUC__ == 2
            (str.compare(start, 0, start.length()) == 0);
            #else
            (str.compare(0, start.length(), start) == 0);
            #endif
}

bool StringUtil::endsWith(const std::string &str, const std::string &end) {
    return
            (end.length() <= str.length()) &&
            #if __GNUC__ == 2
            (str.compare(end, str.length() - end.length(), end.length()) == 0);
            #else
            (str.compare(str.length() - end.length(), end.length(), end) == 0);
            #endif
}

void StringUtil::appendNumber(std::string &str, unsigned int num) {
    int len;
    // 计算 num 的长度
    if (num > 0) {
        len = 0;
        for (unsigned int copy = num; copy > 0; copy /= 10) {
            len++;
        }
    } else {
        len = 1;
    }
    // 在 str 上添加换行符
    str.append(len, '\0');
    // 将换行符替换成数字
    char *ptr = (char *) str.data() + str.length() - 1;
    for (int i = 0; i < len; ++i) {
        *ptr-- = '0' + num % 10;
        num /= 10;
    }
}

void StringUtil::asciiToLowerInline(std::string &asciiString) {
    for (int i = asciiString.size() - 1; i >= 0; --i) {
        asciiString[i] = std::tolower(asciiString[i]);
    }
}