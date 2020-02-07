//
// Created by 陈广祥 on 2019-09-18.
//

#include <jni.h>
#include <string>
#include <android/asset_manager_jni.h>
#include "util/AndroidUtil.h"
#include "plugin/PluginManager.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"
#include "filesystem/asset/AssetManager.h"
#include "android/filesystem/AndroidAssetManager.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_BookPluginManager_registerAssetManager(
        JNIEnv *env,
        jobject instance,
        jobject manager) {
    AssetManager *assetManagerPtr = &AndroidAssetManager::getInstance();
    AndroidAssetManager *androidAssetManager = static_cast<AndroidAssetManager *>(assetManagerPtr);
    // 注册 asset
    androidAssetManager->registerAssetManager(manager);
}

/**
 * 获取当前项目中可用的插件类型
 */
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_BookPluginManager_getSupportPluginTypes(
        JNIEnv *env,
        jobject instance) {
    using namespace std;
    // 获取支持的插件类型
    vector<const string> pluginTypes;
    // 支持的编码类型就是插件类型
    PluginManager::readSupportFormat(pluginTypes);

    size_t pluginTypeSize = pluginTypes.size();

    // 创建 Java 层的数组
    jobjectArray jPluginTypes = env->NewObjectArray(pluginTypeSize,
                                                    AndroidUtil::Class_String.getJClass(), 0);

    // 将 cString 转换成 jString
    for (size_t i = 0; i < pluginTypeSize; ++i) {
        const string &pluginType = pluginTypes[i];

        jstring jPluginType = AndroidUtil::toJString(env, pluginType);
        // 添加到数组中
        env->SetObjectArrayElement(jPluginTypes, i, jPluginType);
        // 释放
        env->DeleteLocalRef(jPluginType);
    }
    return jPluginTypes;
}