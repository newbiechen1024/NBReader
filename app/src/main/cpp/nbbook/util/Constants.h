// author : newbiechen
// date : 2019-11-17 14:56
// description : 
//

#ifndef NBREADER_CONSTANTS_H
#define NBREADER_CONSTANTS_H

#include <string>
namespace Constants{
    // 段落基础信息文件尾缀
    static std::string SUFFIX_PGH_BASE = "pgb";
    // 段落详细信息文件尾缀
    static std::string SUFFIX_PGH_DETAIL = "pgd";
    // 书籍默认缓冲区的大小
    static size_t BOOK_DEFAULT_BUFFER = 131072;
}


#endif //NBREADER_CONSTANTS_H
