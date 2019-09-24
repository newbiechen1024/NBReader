// author : newbiechen
// date : 2019-09-22 20:14
// description : 
//

#include "FileSystem.h"

#if defined(__linux__)
std::string FileSystem::separator= "/";
std::string FileSystem::archiveSeparator = ":";
#elif defined(_WIN32)
std::string FileSystem::separator = "\\";
std::string FileSystem::archiveSeparator = ":";
#endif

FileSystem *FileSystem::sInstance = nullptr;

void FileSystem::deleteInstance() {
    if (sInstance != nullptr){
        delete (sInstance);
        sInstance = nullptr;
    }
}