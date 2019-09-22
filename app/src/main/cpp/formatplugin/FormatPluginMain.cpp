// author : newbiechen
// date : 2019-09-22 15:34
// description : 
//
#include <jni.h>
#include "util/AndroidUtil.h"


/**
 * 初始化 JNI
 */
extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    AndroidUtil::init(jvm);
    return JNI_VERSION_1_6;
}