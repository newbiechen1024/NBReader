// author : newbiechen
// date : 2019-09-25 16:35
// description : Zip 元素管理器
//

#ifndef NBREADER_ZIPFILEMANAGER_H
#define NBREADER_ZIPFILEMANAGER_H

#include <memory>
#include "ZipEntry.h"

class ZipEntryManager {
public:
    static ZipEntryManager &getInstance();

    static void deleteInstance();

    ~ZipEntryManager() {
    }

    std::shared_ptr<ZipEntry> getZipEntry(const std::string &zipPath);

private:
    static ZipEntryManager *sInstance;
    static const std::size_t MAX_CACHE_COUNT = 5;
    // zip 文件缓冲数组
    std::shared_ptr<ZipEntry> mZipEntryArr[MAX_CACHE_COUNT];
    // 缓冲的索引
    int mCacheIndex;

    ZipEntryManager() {
        memset(mZipEntryArr, '\0', MAX_CACHE_COUNT);
        mCacheIndex = 0;
    }
};


#endif //NBREADER_ZIPFILEMANAGER_H
