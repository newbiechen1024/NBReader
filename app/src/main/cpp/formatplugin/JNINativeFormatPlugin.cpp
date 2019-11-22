// author : newbiechen
// date : 2019-09-20 10:58
// description : 
//

#include <jni.h>
#include <string>
#include <plugin/PluginManager.h>
#include <reader/book/Book.h>
#include <reader/bookmodel/BookModel.h>
#include <util/Logger.h>
#include <util/StringUtil.h>
#include "util/AndroidUtil.h"
#include "util/JNIEnvelope.h"
#include "plugin/FormatPlugin.h"

static std::shared_ptr<FormatPlugin> findCppPlugin(jobject base) {
    // 获取调用该方法的 NativePlugin 对应的 type
    std::string formatTypeStr = AndroidUtil::Method_NativeFormatPlugin_getSupportTypeByStr->callForCppString(
            base);
    // 根据 type 查找并返回数据
    return PluginManager::getInstance().getPluginByType(strToFormatType(formatTypeStr));
}

static jobject createJavaTextModel(JNIEnv *env, jobject jBookModel, TextModel &textModel) {
    // 创建一块本地作用域，
    env->PushLocalFrame(16);

    // 获取 textModel 的基础信息
    jstring id = AndroidUtil::toJString(env, textModel.id());
    jstring lang = AndroidUtil::toJString(env, textModel.language());
    const TextCachedAllocator &allocator = textModel.allocator();

    jstring cacheDir = AndroidUtil::toJString(env, allocator.directoryName());
    jstring fileExtension = AndroidUtil::toJString(env, allocator.fileExtension());
    jint blockCount = (jint) allocator.getBufferBlockCount();


    // TODO：展示创建 Paragraph 构造并返回。(没有想到更好的办法，不过我感觉肯定要改的)

    JavaClass &javaTextParagraphInfo = AndroidUtil::Class_TextParagraphInfo;

    size_t paragraphCount = textModel.getParagraphCount();

    jobjectArray jParagraphArr = env->NewObjectArray(paragraphCount,
                                                     javaTextParagraphInfo.getJClass(), 0);

    for (int i = 0; i < paragraphCount; ++i) {
        TextParagraph paragraph = (*textModel[i]);


        jobject jTextParagraphInfo = AndroidUtil::Constructor_TextParagraphInfo->call(
                paragraph.type, paragraph.bufferBlockIndex, paragraph.bufferBlockOffset,
                paragraph.entryCount, paragraph.textLength, paragraph.curTotalTextLength
        );

        env->SetObjectArrayElement(jParagraphArr, i, jTextParagraphInfo);

        env->DeleteLocalRef(jTextParagraphInfo);
    }

    // 调用 creatTextModel
    jobject jTextModel = AndroidUtil::Method_BookModel_createTextModel->call(jBookModel, id, lang,
                                                                             blockCount, cacheDir,
                                                                             fileExtension,
                                                                             jParagraphArr);
    if (env->ExceptionCheck()) {
        // 输出异常描述
        env->ExceptionDescribe();

        jTextModel = 0;
    }

    // 清除本地作用域
    return env->PopLocalFrame(jTextModel);
}


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
}