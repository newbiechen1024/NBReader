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