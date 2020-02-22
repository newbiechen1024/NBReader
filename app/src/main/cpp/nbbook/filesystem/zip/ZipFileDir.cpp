// author : newbiechen
// date : 2019-09-25 16:34
// description : 
//

#include "ZipFileDir.h"
#include "ZipEntry.h"
#include "ZipEntryManager.h"
#include "../FileSystem.h"

void ZipFileDir::readFilePaths(std::vector<std::string> &paths, bool fullPath) const {
    // 从 Zip 管理器中获取 ZipEntry 信息
    std::shared_ptr<ZipEntry> entry = ZipEntryManager::getInstance().getZipEntry(getPath());

    // 将 item 的名字存储到 path 中
    entry->readItemNames(paths);

    // 如果请求的是完整路径，需要添加前缀
    if (fullPath) {
        for (auto it = paths.begin(); it != paths.end(); ++it) {
            *it = fileNameToPath(*it);
        }
    }
}

std::string ZipFileDir::getSeparator() const {
    return FileSystem::archiveSeparator;
}