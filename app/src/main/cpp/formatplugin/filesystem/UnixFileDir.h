// author : newbiechen
// date : 2019-09-25 15:49
// description : 
//

#ifndef NBREADER_UNIXFILEDIR_H
#define NBREADER_UNIXFILEDIR_H


#include "FileDir.h"

#include <string>

class UnixFileDir : public FileDir {
public:

    UnixFileDir(const std::string &path) : FileDir(path) {
    }

    ~UnixFileDir() {
    }

protected:
    virtual void readFilePaths(std::vector<std::string> &paths, bool fullPath);
};


#endif //NBREADER_UNIXFILEDIR_H
