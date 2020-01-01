// author : newbiechen
// date : 2019-09-20 10:58
// description : 
//

#include <jni.h>
#include <string>
#include <plugin/PluginManager.h>
#include <reader/book/Book.h>
#include <util/Logger.h>
#include <util/StringUtil.h>
#include <map>
#include <reader/text/TextBufferAllocator.h>
#include <plugin/txt/TxtReader.h>
#include "util/AndroidUtil.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"


/**
 * 解析 book 并将数据赋值给 BookModel
 * @return
 *
 * 0: 正常
 * 1: 没有与 Book 匹配的解析器
 * 2: 解析书本失败
 */
/*extern "C"
JNIEXPORT jint JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_readModelNative(
        JNIEnv *env,
        jobject instance,
        jobject jBookModel,
        jstring cacheDir_,
        jstring cacheName_) {
    using namespace std;

    // 根据当前 Plugin 查找对应的 cpp Plugin
    shared_ptr<FormatPlugin> formatPlugin = findCppPlugin(instance);

    // 如果查找不到对应的 plugin
    if (!formatPlugin) {
        return 1;
    }


     // 获取缓存路径
     string cacheDir = AndroidUtil::toCString(env, cacheDir_);
     string cacheName = AndroidUtil::toCString(env, cacheName_);

     // 获取 BookModel 中的 book 实例
     jobject jBook = AndroidUtil::Method_BookModel_getBook->call(jBookModel);

     // 根据 Java 层的 book 创建 C++ 层的 Book
     shared_ptr<Book> book = Book::createByJavaBook(jBook);
     env->DeleteLocalRef(jBook);

     // 创建 C++ 层的 BookModel
     shared_ptr<BookModel> bookModel = make_shared<BookModel>(book, jBookModel, cacheDir, cacheName);

     // 使用 plugin 解析 BookModel
     if (!formatPlugin->readModel(*bookModel)) {
         return 2;
     }


     // 强制刷新数据存储
     if (!bookModel->flush()) {
         return 3;
     }


     // 获取 java 的 textModel
     shared_ptr<TextModel> textModel = bookModel->getTextModel();
     jobject jTextModel = createJavaTextModel(env, jBookModel, *textModel);
     if (jTextModel == 0) {
         return 5;
     }


     // 将 java 的 textModel 传递给 bookModel
     AndroidUtil::Method_BookModel_setTextModel->call(jBookModel, jTextModel);
     // 检测
     if (env->ExceptionCheck()) {
         return 6;
     }


     // 删除本地指针
     env->DeleteLocalRef(jTextModel);


    // 初始化超链接

    // 初始化 toc

    // 获取 TextModel 文本，并设置给 BookModel
    // shared_ptr<TextModel> textModel = bookModel->getTextModel();
    // footnotes ==> 注脚是啥东西
    // 字体设置


    return 0;
}*/

static std::map<int, std::shared_ptr<FormatPlugin>> sPluginMap;
static int sPluginIndex = 1; // 假设默认从 1 开始，之后搞一个唯一 id，通过 md5

static const std::string TAG = "JNINativeFormatPlugin";

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_createFormatPluginNative(
        JNIEnv *env, jobject thiz, jstring format_type) {
    // TODO: implement createFormatPluginNative()

/*    // 获取调用该方法的 NativePlugin 对应的 type
    std::string formatTypeStr = AndroidUtil::toCString(env, format_type);

    // 根据 type 查找并返回数据
    std::shared_ptr<FormatPlugin> pluginPtr = PluginManager::createFormatPlugin(formatTypeStr);

    int pluginIndex = sPluginIndex;

    // 插入到 map 中缓存
    sPluginMap.insert(std::pair<int, std::shared_ptr<FormatPlugin>>(pluginIndex, pluginPtr));

    sPluginIndex++;*/

    // 地址

    std::shared_ptr<FormatPlugin> pluginPtr = PluginManager::createFormatPlugin("txt");

    // 测试书籍
    std::string bookPath = "/sdcard/测试书籍/zxczxc.txt";

    std::string cachePath = "";
    std::string pattern = "(?m)^(.{0,8})(\xe7\xac\xac)([0-9\xe9\x9b\xb6\xe4\xb8\x80\xe4\xba\x8c\xe4\xb8\xa4\xe4\xb8\x89\xe5\x9b\x9b\xe4\xba\x94\xe5\x85\xad\xe4\xb8\x83\xe5\x85\xab\xe4\xb9\x9d\xe5\x8d\x81\xe7\x99\xbe\xe5\x8d\x83\xe4\xb8\x87\xe5\xa3\xb9\xe8\xb4\xb0\xe5\x8f\x81\xe8\x82\x86\xe4\xbc\x8d\xe9\x99\x86\xe6\x9f\x92\xe6\x8d\x8c\xe7\x8e\x96\xe6\x8b\xbe\xe4\xbd\xb0\xe4\xbb\x9f]{1,10})([\xe7\xab\xa0\xe8\x8a\x82\xe5\x9b\x9e\xe9\x9b\x86\xe5\x8d\xb7])(.{0,30})$";

    pluginPtr->setConfigure(cachePath, pattern);
    // 设置书籍资源
    pluginPtr->setBookResource(bookPath);

    // 读取章节信息
    std::vector<TextChapter> chapters;

    bool result;

    result = pluginPtr->readChapters(chapters);

    if (result) {
        // 测试章节内容是否编码，并存储到一个缓存位置

        auto chapter = chapters[0];

        Logger::i(TAG,
                  "createFormatPluginNative: chapter" + std::to_string(chapter.startIndex) + "  " +
                  std::to_string(chapter.endIndex));

        char *buffer;
        size_t bufferSize;
        result = pluginPtr->readChapterContent(chapters[0], &buffer, &bufferSize);

        // 将 buffer 输出存储到本地文件
        if (result) {
            File file(
                    "/storage/emulated/0/Android/data/com.example.newbiechen.nbreader/cache/plugin.txt");

            if (!file.exists()) {
                file.createFile();
            }

            auto outputStreamPtr = file.getOutputStream();

            if (outputStreamPtr->open()) {
                outputStreamPtr->write(buffer, bufferSize);
                Logger::i(TAG, "createFormatPluginNative: success");
            }
            Logger::i(TAG, "createFormatPluginNative: " + std::to_string(bufferSize));
        } else {
            Logger::i(TAG, "createFormatPluginNative: chapter content failure");
        }

        delete[]buffer;
    }
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_releaseFormatPluginNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    // 释放 plugin 对象
    // sPluginMap.erase(plugin_desc);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_setPluginConfigureNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    auto pluginPtr = sPluginMap[plugin_desc];
/*    if (pluginPtr != nullptr) {
        pluginPtr->setConfigure()
    }*/
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_setBookSourceNative(
        JNIEnv *env, jobject thiz, jint plugin_desc, jstring book_path) {

    auto pluginPtr = sPluginMap[plugin_desc];
    if (pluginPtr != nullptr) {
        std::string bookPath = AndroidUtil::toCString(env, book_path);
        pluginPtr->setBookResource(bookPath);
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getEncodingNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    auto pluginPtr = sPluginMap[plugin_desc];
    std::string encoding("");
    if (pluginPtr != nullptr) {
        pluginPtr->readEncoding(encoding);
    }
    return AndroidUtil::toJString(env, encoding);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getLanguageNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    auto pluginPtr = sPluginMap[plugin_desc];
    std::string lang("");
    if (pluginPtr != nullptr) {
        pluginPtr->readLanguage(lang);
    }
    return AndroidUtil::toJString(env, lang);
}

/*extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getChaptersNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    // TODO: implement getChapters()

}*/

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_readChapterContentNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    // TODO:应该返回一个数组的
}