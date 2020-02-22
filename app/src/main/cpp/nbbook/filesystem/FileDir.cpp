// author : newbiechen
// date : 2019-09-25 15:19
// description : 
//

#include "FileDir.h"
#include "FileSystem.h"


FileDir::FileDir(const std::string &path) : mPath(path) {
}

void FileDir::readFileNames(std::vector<std::string> &names) const {
    readFilePaths(names, false);
}

void FileDir::readFilePaths(std::vector<std::string> &paths) const {
    readFilePaths(paths, true);
}

std::string FileDir::fileNameToPath(const std::string &name) const {
    // TODO:需要 ../xxx 的情况
    // TODO:需要考虑将 path 当做 name 传入的情况。 如 /xxx/text.xx/xx.aas
    // TODO：需要考虑传入的 .. 返回上一级的情况。
    return mPath + getSeparator() + name;
}

std::string FileDir::getSeparator() const {
    return FileSystem::separator;
}