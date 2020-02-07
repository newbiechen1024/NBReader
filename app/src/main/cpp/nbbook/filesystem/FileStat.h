// author : newbiechen
// date : 2019-09-23 11:21
// description : 
//

#ifndef NBREADER_FILEINFO_H
#define NBREADER_FILEINFO_H

#include <string>
#include <stddef.h>

struct FileStat {
    bool isDirectory;
    bool exists;
    size_t size;
    size_t lastModifiedTime;

    FileStat();
};

inline FileStat::FileStat() : exists(false), isDirectory(false), size(0), lastModifiedTime(0) {
}

#endif //NBREADER_FILEINFO_H
