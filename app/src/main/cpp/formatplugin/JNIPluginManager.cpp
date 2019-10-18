//
// Created by 陈广祥 on 2019-09-18.
//

#include <jni.h>
#include <string>
#include <android/filesystem/AndroidAssetManager.h>
#include "util/AndroidUtil.h"
#include "plugin/PluginManager.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_BookPluginManager_registerAssetManager(JNIEnv *env,
                                                                                                     jobject instance,
                                                                                                     jobject manager) {
    // 向下转型 ==> 不知道会不会崩溃....
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
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_BookPluginManager_getPluginTypes(JNIEnv *env,
                                                                                               jobject instance) {
    using namespace std;
    // 获取插件列表
    vector<shared_ptr<FormatPlugin>> plugins = PluginManager::getInstance().getPlugins();
    size_t pluginSize = plugins.size();

    // 创建 Java 层的数组
    jobjectArray jPluginTypes = env->NewObjectArray(pluginSize, AndroidUtil::Class_String.getJClass(), 0);

    // 循环创建 NativeFormatPlugin
    for (size_t i = 0; i < pluginSize; ++i) {
        // 获取 native 支持的 bookType
        FormatType type = plugins[i]->supportType();
        jstring formatType = AndroidUtil::toJString(env, formatTypeToStr(type));
        // 添加到数组中
        env->SetObjectArrayElement(jPluginTypes, i, formatType);
        // 释放
        env->DeleteLocalRef(formatType);
    }
    return jPluginTypes;
}

/**
 * 释放 PluginManager
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_BookPluginManager_freePlugins(JNIEnv *env,
                                                                                            jobject instance) {
    PluginManager::deleteInstance();
}