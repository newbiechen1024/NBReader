// author : newbiechen
// date : 2019-09-24 16:22
// description : 
//

#ifndef NBREADER_STRINGUTIL_H
#define NBREADER_STRINGUTIL_H

#include <string>

class StringUtil {
private:
    StringUtil();

public:
    static bool startsWith(const std::string &str, const std::string &start);
    static bool endsWith(const std::string &str, const std::string &end);
};


#endif //NBREADER_STRINGUTIL_H
