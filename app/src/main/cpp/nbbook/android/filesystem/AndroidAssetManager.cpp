// author : newbiechen
// date : 2019-10-17 18:09
// description : 
//

#include <vector>
#include "AndroidAssetManager.h"
#include "AndroidAssetInputStream.h"
#include "../../util/AndroidUtil.h"

void AndroidAssetManager::newInstance() {
    if (sInstance == nullptr) {
        sInstance = new AndroidAssetManager();
    }
}

void AndroidAssetManager::registerAssetManager(jobject assetManager) {
    mAssetManager = AndroidUtil::getEnv()->NewGlobalRef(assetManager);
}

AndroidAssetManager::AndroidAssetManager() : mAssetManager(nullptr) {
}

AndroidAssetManager::~AndroidAssetManager() {
    if (mAssetManager != nullptr) {
        AndroidUtil::getEnv()->DeleteGlobalRef(mAssetManager);
    }
}

std::shared_ptr<InputStream> AndroidAssetManager::open(const std::string &path) const {
    AAssetManager *aAssetManager = AAssetManager_fromJava(AndroidUtil::getEnv(), mAssetManager);
    std::shared_ptr<AndroidAssetInputStream> assetStream(new AndroidAssetInputStream(aAssetManager, path));
    return assetStream;
}

std::shared_ptr<std::vector<std::string>> AndroidAssetManager::list(const std::string &path, bool fullPath) const {
    std::shared_ptr<std::vector<std::string>> filePaths(new std::vector<std::string>());

    AAssetManager *aAssetManager = AAssetManager_fromJava(AndroidUtil::getEnv(), mAssetManager);
    if (aAssetManager == nullptr) {
        return filePaths;
    }

    AAssetDir *assetDir = AAssetManager_openDir(aAssetManager, path.c_str());
    if (assetDir == nullptr) {
        return filePaths;
    }

    const char *fileName = nullptr;
    // 循环获取文件名
    while ((fileName = AAssetDir_getNextFileName(assetDir)) != nullptr) {
        std::string filePath(fileName, fileName + strlen(fileName));
        if (fullPath) {
            filePath = path + filePath;
        }
        filePaths->push_back(filePath);
    }

    AAssetDir_close(assetDir);
    return filePaths;
}
