// author : newbiechen
// date : 2019-09-20 10:58
// description : 
//

#include <jni.h>
#include <string>
#include "plugin/FormatPlugin.h"
#include "AndroidUtil.h"

static std::shared_ptr<FormatPlugin> findCppPlugin(jobject base) {
    // 获取调用该方法的 NativePlugin 对应的 type

    // 根据 type 查找并返回数据
}

/**
 * 解析 book 并将数据赋值给 BookModel
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPluginK_readModelNative(JNIEnv *env,
                                                                                                  jobject instance,
                                                                                                  jobject bookModel,
                                                                                                  jstring cacheDir_) {
    const char *cacheDir = env->GetStringUTFChars(cacheDir_, 0);

    // TODO

    env->ReleaseStringUTFChars(cacheDir_, cacheDir);
}
