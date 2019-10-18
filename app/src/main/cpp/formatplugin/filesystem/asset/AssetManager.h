// author : newbiechen
// date : 2019-10-17 15:25
// description : 资源管理器
//

#ifndef NBREADER_ASSETMANAGER_H
#define NBREADER_ASSETMANAGER_H

#include <string>
#include "../io/InputStream.h"

class AssetManager {
public:
    static AssetManager &getInstance() {
        return *sInstance;
    }

    static void deleteInstance() {
        delete sInstance;
    }

public:
    // 输入流
    virtual std::shared_ptr<InputStream> open(const std::string &path) const = 0;

    /**
     * 获取文件目录下的所有文件路径
     * @param path ：资源文件路径
     * @param fullPath:返回全路径，还是返回文件名
     * @return
     */
    virtual std::shared_ptr<std::vector<std::string>> list(const std::string &path, bool fullPath) const = 0;

protected:
    static AssetManager *sInstance;
};


#endif //NBREADER_ASSETMANAGER_H
