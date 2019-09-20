//
// Created by 陈广祥 on 2019-09-19.
//

#include "AndroidUtil.h"
#include "JNIEnvelope.h"

const std::string PKG_NAME = "com/example/newbiechen/nbreader/";

JavaClass AndroidUtil::Class_String("java/lang/String");

JavaClass AndroidUtil::Class_NativeFormatPlugin(
        PKG_NAME + "ui/component/book/plugin/NativeFormatPlugin");

JavaClass AndroidUtil::Class_FormatPluginManager(
        PKG_NAME + "ui/component/book/plugin/FormatPluginManager");

JavaClass AndroidUtil::Class_BookModel(PKG_NAME + "ui/component/book/entity/BookModel");

JavaClass AndroidUtil::Class_Book(PKG_NAME + "data/entity/book");

JNIEnv *AndroidUtil::getEnv() {
    JNIEnv *env;
    sJavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);
    return env;
}

bool AndroidUtil::init(JavaVM *jvm) {
    sJavaVM = jvm;
    Method_NativeFormatPlugin_getSupportTypeByStr = new StringMethod(Class_NativeFormatPlugin, "getSupportTypeByStr",
                                                                     "()");
    Method_BookModel_getBook = new ObjectMethod(Class_BookModel, "getBook", Class_Book, "()");
    return true;
}

jstring AndroidUtil::toJString(JNIEnv *env, const std::string &str) {
    if (str.empty()) {
        return 0;
    }
    return env->NewStringUTF(str.c_str());
}

std::string AndroidUtil::toCString(JNIEnv *env, jstring from) {
    if (from == 0) {
        return std::string();
    }
    const char *data = env->GetStringUTFChars(from, 0);
    const std::string result(data);
    env->ReleaseStringUTFChars(from, data);
    return result;
}