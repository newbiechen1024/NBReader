// author : newbiechen
// date : 2019-10-14 11:10
// description : 
//

#include <jni.h>
#include <util/AndroidUtil.h>
#include <android/filesystem/AndroidFileSystem.h>
#include "AndroidFormatPluginApp.h"

void AndroidFormatPluginApp::newInstance() {
    sInstance = new AndroidFormatPluginApp();
}

std::string AndroidFormatPluginApp::language() {
    JNIEnv *env = AndroidUtil::getEnv();
    jobject locale = AndroidUtil::StaticMethod_Locale_getDefault->call();
    std::string lang = AndroidUtil::Method_Locale_getLanguage->callForCppString(locale);
    env->DeleteLocalRef(locale);
    return lang;
}

std::string AndroidFormatPluginApp::version() {
    // TODO:暂时未实现
    return std::string();
}

void AndroidFormatPluginApp::initApp(JavaVM *jvm) {
    // 初始化 android 工具类
    AndroidUtil::init(jvm);
    // 初始化 android 文件系统
    AndroidFileSystem::newInstance();
}
