// author : newbiechen
// date : 2019-10-17 16:57
// description : 
//

#ifndef NBREADER_LANGUTIL_H
#define NBREADER_LANGUTIL_H

#include <string>

class LangUtil {
public:
    // 获取 language 的匹配资源文件目录
    static std::string getPatternDirectoryFromAsset();

    static const std::vector<std::string> &languageCodes();
};


#endif //NBREADER_LANGUTIL_H
