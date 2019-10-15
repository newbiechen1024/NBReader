// author : newbiechen
// date : 2019-09-25 16:34
// description : 
//

#include <filesystem/FileSystem.h>
#include "ZipFileDir.h"
#include "ZipEntry.h"
#include "ZipEntryManager.h"

void ZipFileDir::readFilePaths(std::vector<std::string> &paths, bool fullPath) const {
    // 从管理器中获取 ZipEntry
    std::shared_ptr<ZipEntry> entry = ZipEntryManager::getInstance().getZipEntry(getPath());
    // 将 item 的名字存储到 path 中
    entry->readItemNames(paths);
}

std::string ZipFileDir::getSeparator() const {
    return FileSystem::archiveSeparator;
}