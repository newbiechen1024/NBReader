// author : newbiechen
// date : 2019-09-20 10:58
// description : 
//

#include <jni.h>
#include <string>
#include <plugin/PluginManager.h>
#include <reader/book/Book.h>
#include <reader/bookmodel/BookModel.h>
#include "util/AndroidUtil.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"

static std::shared_ptr<FormatPlugin> findCppPlugin(jobject base) {
    // 获取调用该方法的 NativePlugin 对应的 type
    std::string formatTypeStr = AndroidUtil::Method_NativeFormatPlugin_getSupportTypeByStr->callForCppString(base);
    // 根据 type 查找并返回数据
    return PluginManager::getInstance().getPluginByType(strToFormatType(formatTypeStr));
}

/*static jobject createJavaTextModel(JNIEnv *env, jobject jBookModel, TextModel &textModel) {
    // 创建一块本地作用域
    // env->PushLocalFrame(16);
    // 将 bookModel 中的实例转成 jObject
}*/



/**
 * 解析 book 并将数据赋值给 BookModel
 * @return
 *
 * 0: 正常
 * 1: 没有与 Book 匹配的解析器
 * 2: 解析书本失败
 */
extern "C"
JNIEXPORT jint JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_readModelNative(JNIEnv *env,
                                                                                                 jobject instance,
                                                                                                 jobject jBookModel,
                                                                                                 jstring cacheDir_) {
    using namespace std;

    // 根据当前 Plugin 查找对应的 cpp Plugin
    shared_ptr<FormatPlugin> formatPlugin = findCppPlugin(instance);

    // 如果查找不到对应的 plugin
    if (!formatPlugin) {
        return 1;
    }

    // 获取缓存路径
    string cacheDir = AndroidUtil::toCString(env, cacheDir_);
    // 获取 BookModel 中的 book 实例
    jobject jBook = AndroidUtil::Method_BookModel_getBook->call(jBookModel);
    // 根据 Java 层的 book 创建 C++ 层的 Book
    shared_ptr<Book> book = Book::createByJavaBook(jBook);
    // 创建 C++ 层的 BookModel
    shared_ptr<BookModel> bookModel = make_shared<BookModel>(book, jBookModel, cacheDir);
    // 使用 plugin 解析 BookModel
    if (!formatPlugin->readModel(*bookModel)) {
        return 2;
    }

    // 初始化超链接

    // 初始化 toc

    // 获取 TextModel 文本，并设置给 BookModel
    shared_ptr<TextModel> textModel = bookModel->getTextModel();
    // footnotes ==> 注脚是啥东西

    // 字体设置
    return 0;
}