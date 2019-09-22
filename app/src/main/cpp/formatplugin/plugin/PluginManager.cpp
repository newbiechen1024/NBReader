//
// Created by 陈广祥 on 2019-09-18.
//

#include <plugin/txt/TxtPlugin.h>
#include "PluginManager.h"

PluginManager *PluginManager::sInstance = 0;

PluginManager &PluginManager::getInstance() {
    // 如果 sInstance 未初始化
    if (sInstance == 0) {
        sInstance = new PluginManager();
    }
    return *sInstance;
}

void PluginManager::deleteInstance() {
    // 释放实例
    if (sInstance != 0) {
        delete (sInstance);
    }
}

PluginManager::PluginManager() {
    // 初始化插件
    mPluginList.push_back(std::make_shared<TxtPlugin>());
}

PluginManager::~PluginManager() {
    // 不处理
}

std::shared_ptr<FormatPlugin> PluginManager::getPluginByType(FormatType type) const {
    for (auto it = mPluginList.begin(); it != mPluginList.end(); ++it) {
        if (type == (*it)->supportType()) {
            return *it;
        }
    }

    return nullptr;
}