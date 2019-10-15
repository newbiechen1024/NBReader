// author : newbiechen
// date : 2019-09-25 15:49
// description : 
//

#include <dirent.h>
#include <sys/stat.h>
#include "UnixFileDir.h"
#include "FileSystem.h"

void UnixFileDir::readFilePaths(std::vector<std::string> &paths, bool fullPath) const {
    DIR *dir = opendir(getPath().c_str());

    if (dir != 0) {
        const std::string namePrefix = getPath() + FileSystem::separator;
        const dirent *file;
        struct stat fileInfo;
        std::string shortName;
        while ((file = readdir(dir)) != 0) {
            shortName = file->d_name;
            if ((shortName == ".") || (shortName == "..")) {
                continue;
            }
            const std::string path = namePrefix + shortName;
            lstat(path.c_str(), &fileInfo);
            if (S_ISREG(fileInfo.st_mode)) {
                paths.push_back(fullPath ? path : shortName);
            }
        }
        closedir(dir);
    }
}