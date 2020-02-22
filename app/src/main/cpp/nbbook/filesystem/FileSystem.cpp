// author : newbiechen
// date : 2019-09-22 20:14
// description : 
//

#include "FileSystem.h"

#if defined(__linux__)
const std::string FileSystem::separator = "/";
const std::string FileSystem::archiveSeparator = ":";
#elif defined(_WIN32)
std::string FileSystem::separator = "\\";
std::string FileSystem::archiveSeparator = ":";
#endif

FileSystem *FileSystem::sInstance = nullptr;

void FileSystem::deleteInstance() {
    if (sInstance != nullptr) {
        delete (sInstance);
        sInstance = nullptr;
    }
}

void FileSystem::normalize(std::string &path) {
    // 处理压缩包的问题
    int archiveIndex = findArchiveNameDelimiter(path);
    if (path.empty() || archiveIndex == -1) {
        normalizeInternal(path);
    } else {
        std::string realPath = path.substr(0, archiveIndex);
        std::string archivePath = path.substr(archiveIndex + 1);
        normalizeInternal(realPath);
        path = realPath + FileSystem::archiveSeparator + normalizePath(archivePath);
    }
}

int FileSystem::findArchiveNameDelimiter(const std::string &path) const {
    return path.rfind(FileSystem::archiveSeparator);
}

int FileSystem::findLastNameDelimiter(const std::string &path) const {
    int index = findArchiveNameDelimiter(path);
    if (index == -1) {
        index = path.rfind(FileSystem::separator);
    }
    return index;
}