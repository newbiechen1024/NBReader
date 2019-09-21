//
// Created by 陈广祥 on 2019-09-18.
//

#include <jni.h>
#include <string>
#include "util/AndroidUtil.h"
#include "plugin/PluginManager.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"

/**
 * 初始化 JNI
 */
extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    AndroidUtil::init(jvm);
    return JNI_VERSION_1_6;
}

/**
 * 获取当前项目中可用的插件类型
 */
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_FormatPluginManager_getPluginTypes(JNIEnv *env,
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
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_FormatPluginManager_freePlugins(JNIEnv *env,
                                                                                              jobject instance) {
    PluginManager::deleteInstance();
}