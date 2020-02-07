// author : newbiechen
// date : 2019-10-17 18:09
// description : 
//

#ifndef NBREADER_ANDROIDASSETMANAGER_H
#define NBREADER_ANDROIDASSETMANAGER_H


#include "../../filesystem/asset/AssetManager.h"
#include <android/asset_manager.h>
#include <vector>
#include <jni.h>

class AndroidAssetManager : public AssetManager {
public:
    static void newInstance();

    void registerAssetManager(jobject assetManager);

    virtual std::shared_ptr<InputStream> open(const std::string &path) const override;


    virtual std::shared_ptr<std::vector<std::string>> list(const std::string &path, bool fullPath) const override;

private:
    AndroidAssetManager();

    ~AndroidAssetManager();

    jobject mAssetManager;

};


#endif //NBREADER_ANDROIDASSETMANAGER_H
