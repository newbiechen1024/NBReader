// author : newbiechen
// date : 2019-10-17 18:11
// description : 
//

#include "AndroidAssetInputStream.h"

AndroidAssetInputStream::AndroidAssetInputStream(AAssetManager *aAssetManager,
                                                 const std::string &assetPath)
        : mAAssetManager(aAssetManager), mAssetPath(assetPath), mAssetFile(nullptr) {
}

AndroidAssetInputStream::~AndroidAssetInputStream() {
    close();
}

bool AndroidAssetInputStream::open() {
    if (mAssetFile == nullptr) {
        mAssetFile = AAssetManager_open(mAAssetManager, mAssetPath.c_str(), AASSET_MODE_BUFFER);
    }
    return mAssetFile != nullptr;
}

size_t AndroidAssetInputStream::read(char *buffer, size_t maxSize) {
    if (mAssetFile != nullptr) {
        return AAsset_read(mAssetFile, buffer, maxSize);
    }
    return 0;
}

void AndroidAssetInputStream::seek(int offset, bool absoluteOffset) {
    // 不处理
    if (mAssetFile != nullptr) {
        AAsset_seek(mAssetFile, offset, absoluteOffset ? SEEK_SET : SEEK_CUR);
    }
}

size_t AndroidAssetInputStream::offset() const {
    return mAssetFile == nullptr ? 0 : (AAsset_getLength(mAssetFile) -
                                        AAsset_getRemainingLength(mAssetFile));
}

size_t AndroidAssetInputStream::length() const {
    return AAsset_getLength(mAssetFile);
}

void AndroidAssetInputStream::close() {
    if (mAssetFile != nullptr) {
        AAsset_close(mAssetFile);
        mAssetFile = nullptr;
    }
}
