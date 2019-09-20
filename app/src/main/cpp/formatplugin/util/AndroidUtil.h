//
// Created by 陈广祥 on 2019-09-19.
// desc：C++ 调用 Java 层的工具

#ifndef NBREADER_ANDROIDUTIL_H
#define NBREADER_ANDROIDUTIL_H


#include <jni.h>
#include <string>
#include <vector>

class JavaClass;

class JavaArray;

class JavaConstructor;

class ObjectField;

class VoidMethod;

class IntMethod;

class LongMethod;

class BooleanMethod;

class StringMethod;

class ObjectMethod;

class ObjectArrayMethod;

class StaticObjectMethod;

class AndroidUtil {
private:
    static JavaVM *sJavaVM;

public:
    static JavaClass Class_String;
    static JavaClass Class_NativeFormatPlugin;
    static JavaClass Class_FormatPluginManager;
    static JavaClass Class_BookModel;
    static JavaClass Class_Book;
    static std::shared_ptr<StringMethod> Method_NativeFormatPlugin_getSupportTypeByStr;
    static std::shared_ptr<ObjectMethod> Method_BookModel_getBook;

public:
    static bool init(JavaVM *jvm);

    static JNIEnv *getEnv();

    static std::string toCString(JNIEnv *env, jstring from);

    static jstring toJString(JNIEnv *env, const std::string &str);

    static jintArray createJavaIntArray(JNIEnv *env, const std::vector<jint> &data);

    static jbyteArray createJavaByteArray(JNIEnv *env, const std::vector<jbyte> &data);
};

#endif //NBREADER_ANDROIDUTIL_H
