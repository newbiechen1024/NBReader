// author : newbiechen
// date : 2019-09-23 11:21
// description : 
//

#ifndef NBREADER_FILEINFO_H
#define NBREADER_FILEINFO_H

#include <string>

struct FileStat {
    bool isDirectory;
    bool exist;
    std::size_t size;
    std::size_t lastModifiedTime;
};

#endif //NBREADER_FILEINFO_H
