// author : newbiechen
// date : 2019-10-17 18:11
// description : 
//

#ifndef NBREADER_ANDROIDASSETINPUTSTREAM_H
#define NBREADER_ANDROIDASSETINPUTSTREAM_H


#include <jni.h>
#include <string>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "../../filesystem/io/InputStream.h"

class AndroidAssetInputStream : public InputStream {
public:

    ~AndroidAssetInputStream();

    virtual bool open() override;

    virtual size_t read(char *buffer, size_t maxSize) override;

    virtual void seek(int offset, bool absoluteOffset) override;

    virtual size_t offset() const override;

    size_t length() const override;

    virtual void close() override;

private:
    AndroidAssetInputStream(AAssetManager *aAssetManager, const std::string &assetPath);


    AAssetManager *mAAssetManager;
    AAsset *mAssetFile;
    const std::string mAssetPath;

    friend class AndroidAssetManager;
};


#endif //NBREADER_ANDROIDASSETINPUTSTREAM_H
