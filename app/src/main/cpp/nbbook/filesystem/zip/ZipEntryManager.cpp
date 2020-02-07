// author : newbiechen
// date : 2019-09-25 16:35
// description : 
//

#include <memory>
#include "ZipEntryManager.h"

ZipEntryManager *ZipEntryManager::sInstance = nullptr;

ZipEntryManager &ZipEntryManager::getInstance() {
    if (sInstance == nullptr) {
        sInstance = new ZipEntryManager();
    }
    return *sInstance;
}

void ZipEntryManager::deleteInstance() {
    if (sInstance != nullptr) {
        delete (sInstance);
        sInstance = nullptr;
    }
}

std::shared_ptr<ZipEntry> ZipEntryManager::getZipEntry(const std::string &zipPath) {
    // 判断缓存中是否存在该 entry
    for (size_t i = 0; i < MAX_CACHE_COUNT; ++i) {
        std::shared_ptr<ZipEntry> cache = mZipEntryArr[i];
        // 如果缓存存在
        if (cache != nullptr && cache->getPath() == zipPath) {
            // 如果缓存失效，则重新加载
            if (!cache->isValid()) {
                cache = std::make_shared<ZipEntry>(zipPath);
                mZipEntryArr[i] = cache;
            }
            return cache;
        }
    }

    // 如果 entry 不存在
    std::shared_ptr<ZipEntry> cache = std::make_shared<ZipEntry>(zipPath);
    mZipEntryArr[mCacheIndex] = cache;
    mCacheIndex = (mCacheIndex + 1) % MAX_CACHE_COUNT;
    return cache;
}