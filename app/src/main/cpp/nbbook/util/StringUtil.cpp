// author : newbiechen
// date : 2019-09-24 16:22
// description : 
//

#include "StringUtil.h"
#include <locale.h>

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


std::string StringUtil::numberToString(unsigned int n) {
    std::string str;
    appendNumber(str, n);
    return str;
}


void StringUtil::asciiToLowerInline(std::string &asciiString) {
    for (int i = asciiString.size() - 1; i >= 0; --i) {
        asciiString[i] = std::tolower(asciiString[i]);
    }
}


void StringUtil::stripWhiteSpaces(std::string &str) {
    std::size_t counter = 0;
    std::size_t length = str.length();
    while (counter < length && std::isspace((unsigned char) str[counter])) {
        counter++;
    }
    str.erase(0, counter);
    length -= counter;

    std::size_t r_counter = length;
    while (r_counter > 0 && std::isspace((unsigned char) str[r_counter - 1])) {
        r_counter--;
    }
    str.erase(r_counter, length - r_counter);
}


std::vector<std::string>
StringUtil::split(const std::string &str, const std::string &delimiter, bool skipEmpty) {
    std::vector<std::string> result;
    std::size_t start = 0;
    std::size_t index = str.find(delimiter);
    while (index != std::string::npos) {
        const std::string sub = str.substr(start, index - start);
        if (!skipEmpty || sub.size() > 0) {
            result.push_back(sub);
        }
        start = index + delimiter.length();
        index = str.find(delimiter, start);
    }
    const std::string sub = str.substr(start, index - start);
    if (!skipEmpty || sub.size() > 0) {
        result.push_back(sub);
    }
    return result;
}


int StringUtil::parseDecimal(const std::string &str, int defaultValue) {
    if (str.empty()) {
        return defaultValue;
    }
    if (!std::isdigit(str[0]) && (str.length() == 1 || str[0] != '-' || !std::isdigit(str[1]))) {
        return defaultValue;
    }

    for (std::size_t i = 1; i < str.length(); ++i) {
        if (!std::isdigit(str[i])) {
            return defaultValue;
        }
    }

    return std::atoi(str.c_str());
}

double StringUtil::stringToDouble(const std::string &str, double defaultValue) {
    if (!str.empty()) {
        setlocale(LC_NUMERIC, "C");
        return std::atof(str.c_str());
    } else {
        return defaultValue;
    }
}