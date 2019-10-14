// author : newbiechen
// date : 2019-09-22 15:34
// description : 
//
#include <jni.h>
#include <android/application/AndroidFormatPluginApp.h>
#include "util/AndroidUtil.h"


/**
 * 初始化 JNI
 */
extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    AndroidFormatPluginApp::newInstance();
    return JNI_VERSION_1_6;
}