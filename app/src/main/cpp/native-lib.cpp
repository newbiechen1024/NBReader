#include <jni.h>
#include <string>
extern "C" JNIEXPORT jstring JNICALL
Java_com_example_newbiechen_nbreader_ui_page_bookshelf_BookShelfFragment_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string value = "hello wolrd";
    return env->NewStringUTF(value.c_str());
}