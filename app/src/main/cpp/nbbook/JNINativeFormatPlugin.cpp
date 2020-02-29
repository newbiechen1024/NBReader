// author : newbiechen
// date : 2019-09-20 10:58
// description : 
//

#include <jni.h>
#include <string>

#include <map>
#include "util/AndroidUtil.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"
#include "plugin/PluginManager.h"


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
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_readModelNative(
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
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_createFormatPluginNative(
        JNIEnv *env, jobject thiz, jstring format_type) {

    // 获取调用该方法的 NativePlugin 对应的 type
    std::string formatTypeStr = AndroidUtil::toCString(env, format_type);

    // 根据 type 查找并返回数据
    std::shared_ptr<FormatPlugin> pluginPtr = PluginManager::createFormatPlugin(formatTypeStr);

    int pluginIndex = sPluginIndex;

    // 插入到 map 中缓存
    sPluginMap.insert(std::pair<int, std::shared_ptr<FormatPlugin>>(pluginIndex, pluginPtr));

    sPluginIndex++;

    return pluginIndex;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_releaseFormatPluginNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    // 释放 plugin 对象
    sPluginMap.erase(plugin_desc);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_setConfigureNative(
        JNIEnv *env, jobject thiz, jint plugin_desc,
        jstring cache_path, jstring chapter_pattern, jstring chapter_prologue_title) {
    auto pluginPtr = sPluginMap[plugin_desc];
    if (pluginPtr != nullptr) {
        std::string cachePath = AndroidUtil::toCString(env, cache_path);
        std::string chapterPattern = AndroidUtil::toCString(env, chapter_pattern);
        std::string chapterPrologueTitle = AndroidUtil::toCString(env, chapter_prologue_title);
        pluginPtr->setConfigure(cachePath, chapterPattern, chapterPrologueTitle);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_setBookSourceNative(
        JNIEnv *env, jobject thiz, jint plugin_desc, jstring book_path) {

    auto pluginPtr = sPluginMap[plugin_desc];
    if (pluginPtr != nullptr) {
        std::string bookPath = AndroidUtil::toCString(env, book_path);
        pluginPtr->setBookResource(bookPath);
    }
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getEncodingNative(
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
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getLanguageNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    auto pluginPtr = sPluginMap[plugin_desc];
    std::string lang("");
    if (pluginPtr != nullptr) {
        pluginPtr->readLanguage(lang);
    }
    return AndroidUtil::toJString(env, lang);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_getChaptersNative(
        JNIEnv *env, jobject thiz, jint plugin_desc) {
    // TODO: implement getChapters()
    auto pluginPtr = sPluginMap[plugin_desc];
    std::vector<TextChapter> chapters;

    if (pluginPtr != nullptr) {
        pluginPtr->readChapters(chapters);
    }

    // 如果章节列表为空，则表示没有请求到返回 null
    if (chapters.empty()) {
        return 0;
    }

    jobjectArray jChapterArr = env->NewObjectArray(
            chapters.size(), AndroidUtil::Class_TextChapter.getJClass(), 0
    );

    for (std::size_t i = 0; i < chapters.size(); ++i) {
        auto chapter = chapters[i];
        jstring jUrl = AndroidUtil::toJString(env, chapter.url);
        jstring jTitle = AndroidUtil::toJString(env, chapter.title);

        jobject jTextChapter = AndroidUtil::Constructor_TextChapter->call(jUrl, jTitle,
                                                                          chapter.startIndex,
                                                                          chapter.endIndex);
        env->SetObjectArrayElement(jChapterArr, i, jTextChapter);
        env->DeleteLocalRef(jTextChapter);
    }
    return jChapterArr;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_newbiechen_nbreader_ui_component_book_plugin_NativeFormatPlugin_readChapterContentNative(
        JNIEnv *env, jobject thiz, jint plugin_desc, jobject text_chapter) {

    std::string chapterUrl = AndroidUtil::Method_TextChapter_getUrl->callForCppString(text_chapter);
    std::string chapterTitle = AndroidUtil::Method_TextChapter_getTitle->callForCppString(
            text_chapter);

    int chapterStartIndex = AndroidUtil::Method_TextChapter_getStartIndex->call(text_chapter);
    int chapterEndIndex = AndroidUtil::Method_TextChapter_getEndIndex->call(text_chapter);

    TextChapter chapter(chapterUrl, chapterTitle, chapterStartIndex, chapterEndIndex);

    // TODO: 错误处理等之后再说
    auto pluginPtr = sPluginMap[plugin_desc];

    if (pluginPtr == nullptr) {
        return 0;
    }

    TextContent textContent;

    // 如果读取章节信息失败
    if (!pluginPtr->readChapterContent(chapter, textContent) && !textContent.isInitialized()) {
        return 0;
    }

    jbyteArray jResourceArr = 0;

    // 资源数据可能不存在
    if (textContent.resourceSize != 0) {
        jResourceArr = env->NewByteArray(textContent.resourceSize);

        env->SetByteArrayRegion(jResourceArr, 0, textContent.resourceSize,
                                (jbyte *) textContent.resourcePtr);
    }

    // 创建内容数据
    jbyteArray jContentArr = env->NewByteArray(textContent.contentSize);

    env->SetByteArrayRegion(jContentArr, 0, textContent.contentSize,
                            (jbyte *) textContent.contentPtr);

    // 创建 content 对象
    jobject jTextContent = AndroidUtil::Constructor_TextContent->call(jResourceArr, jContentArr);

    // 释放内部资源
    textContent.release();

    return jTextContent;
}