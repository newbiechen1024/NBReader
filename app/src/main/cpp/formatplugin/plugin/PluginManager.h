//
// Created by 陈广祥 on 2019-09-18.
//

#ifndef NBREADER_PLUGINMANAGER_H
#define NBREADER_PLUGINMANAGER_H

#include <vector>
#include "FormatPlugin.h"

class PluginManager {

public:
    ~PluginManager();

    static PluginManager &getInstance();

    static void deleteInstance();

    // 获取当前所有插件
    std::vector<std::shared_ptr<FormatPlugin>> getPlugins() const;

    // 根据 type 获取相应插件
    std::shared_ptr<FormatPlugin> getPluginByType(FormatType type) const;

private:
    static PluginManager *sInstance;

    std::vector<std::shared_ptr<FormatPlugin>> mPluginList;

    PluginManager(); // 私有构造
};

inline std::vector<std::shared_ptr<FormatPlugin>> PluginManager::getPlugins() const {
    return mPluginList;
}

#endif //NBREADER_PLUGINMANAGER_H
