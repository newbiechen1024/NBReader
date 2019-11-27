//
// Created by 陈广祥 on 2019-09-19.
//

#include "AndroidUtil.h"
#include "UnicodeUtil.h"

JavaVM *AndroidUtil::sJavaVM = 0;

const std::string PKG_NAME = "com/example/newbiechen/nbreader/";

JavaClass AndroidUtil::Class_String("java/lang/String");

JavaClass AndroidUtil::Class_Locale("java/util/Locale");

JavaClass AndroidUtil::Class_NativeFormatPlugin(
        PKG_NAME + "ui/component/book/plugin/NativeFormatPlugin");

JavaClass AndroidUtil::Class_BoolPluginManager(
        PKG_NAME + "ui/component/book/plugin/BookPluginManager");

JavaClass AndroidUtil::Class_BookModel(PKG_NAME + "ui/component/book/BookModel");

JavaClass AndroidUtil::Class_BookEntity(PKG_NAME + "data/entity/BookEntity");

JavaClass AndroidUtil::Class_TextModel(
        PKG_NAME + "ui/component/book/text/TextModel");

JavaClass AndroidUtil::Class_EncodingConverter(
        PKG_NAME + "ui/component/book/text/util/EncodingConverter"
);

JavaClass AndroidUtil::Class_ChapterDetector(
        PKG_NAME + "ui/component/book/text/util/ChapterDetector"
);

// 初始化静态成员。。
std::shared_ptr<StaticObjectMethod> AndroidUtil::StaticMethod_Locale_getDefault;
std::shared_ptr<StaticBooleanMethod> AndroidUtil::StaticMethod_EncodingConverter_isEncodingSupport;
std::shared_ptr<StaticObjectMethod> AndroidUtil::StaticMethod_EncodingConverter_createEncodingConverter;
std::shared_ptr<StaticObjectMethod> AndroidUtil::StaticMethod_ChapterDetector_createChapterDetector;

// 初始化普通成员
std::shared_ptr<StringMethod> AndroidUtil::Method_Locale_getLanguage;
std::shared_ptr<StringMethod> AndroidUtil::Method_NativeFormatPlugin_getSupportTypeByStr;
std::shared_ptr<ObjectMethod> AndroidUtil::Method_BookModel_getBook;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getTitle;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getUrl;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getEncoding;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getLang;
std::shared_ptr<StringMethod> AndroidUtil::Method_String_toLowerCase;
std::shared_ptr<StringMethod> AndroidUtil::Method_String_toUpperCase;

std::shared_ptr<ObjectMethod> AndroidUtil::Method_BookModel_createTextModel;
std::shared_ptr<VoidMethod> AndroidUtil::Method_BookModel_setTextModel;

std::shared_ptr<StringMethod> AndroidUtil::Method_EncodingConverter_getName;
std::shared_ptr<IntMethod> AndroidUtil::Method_EncodingConverter_convert;
std::shared_ptr<VoidMethod> AndroidUtil::Method_EncodingConverter_reset;
std::shared_ptr<StringMethod> AndroidUtil::Method_ChapterDetector_getRegexStr;


JNIEnv *AndroidUtil::getEnv() {
    JNIEnv *env;
    sJavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);
    return env;
}

bool AndroidUtil::init(JavaVM *jvm) {
    sJavaVM = jvm;

    StaticMethod_Locale_getDefault = std::make_shared<StaticObjectMethod>(Class_Locale,
                                                                          "getDefault",
                                                                          Class_Locale, "()");

    StaticMethod_EncodingConverter_isEncodingSupport = std::make_shared<StaticBooleanMethod>(
            Class_EncodingConverter,
            "isEncodingSupport", "(Ljava/lang/String;)");

    StaticMethod_EncodingConverter_createEncodingConverter = std::make_shared<StaticObjectMethod>(
            Class_EncodingConverter,
            "createEncodingConverter", Class_EncodingConverter, "(Ljava/lang/String;)");

    StaticMethod_ChapterDetector_createChapterDetector = std::make_shared<StaticObjectMethod>(
            Class_ChapterDetector,
            "createChapterDetector", Class_ChapterDetector, "(Ljava/lang/String;)");

    Method_Locale_getLanguage = std::make_shared<StringMethod>(Class_Locale, "getLanguage", "()");

    // string
    Method_String_toLowerCase = std::make_shared<StringMethod>(Class_String, "toLowerCase", "()");
    Method_String_toUpperCase = std::make_shared<StringMethod>(Class_String, "toUpperCase", "()");

    Method_NativeFormatPlugin_getSupportTypeByStr = std::make_shared<StringMethod>(
            Class_NativeFormatPlugin,
            "getSupportTypeByStr",
            "()");
    Method_BookModel_getBook = std::make_shared<ObjectMethod>(Class_BookModel, "getBook",
                                                              Class_BookEntity, "()");

    /*Book*/
    Method_Book_getTitle = std::make_shared<StringMethod>(Class_BookEntity, "getTitle", "()");
    Method_Book_getUrl = std::make_shared<StringMethod>(Class_BookEntity, "getUrl", "()");
    Method_Book_getEncoding = std::make_shared<StringMethod>(Class_BookEntity, "getEncoding", "()");
    Method_Book_getLang = std::make_shared<StringMethod>(Class_BookEntity, "getLang", "()");

    Method_BookModel_createTextModel = std::make_shared<ObjectMethod>(Class_BookModel,
                                                                      "createTextModel",
                                                                      Class_TextModel,
                                                                      "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)");

    Method_BookModel_setTextModel = std::make_shared<VoidMethod>(Class_BookModel,
                                                                 "setTextModel",
                                                                 "(Lcom/example/newbiechen/nbreader/ui/component/book/text/TextModel;)");

    Method_EncodingConverter_convert = std::make_shared<IntMethod>(Class_EncodingConverter,
                                                                   "convert",
                                                                   "([BII[C)");

    Method_EncodingConverter_getName = std::make_shared<StringMethod>(Class_EncodingConverter,
                                                                      "getName", "()");

    Method_EncodingConverter_reset = std::make_shared<VoidMethod>(Class_EncodingConverter, "reset",
                                                                  "()");

    Method_ChapterDetector_getRegexStr = std::make_shared<StringMethod>(Class_ChapterDetector,
                                                                        "getRegexStr",
                                                                        "(Ljava/lang/String;)");
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

std::string AndroidUtil::convertNonUtfString(const std::string &str) {
    if (UnicodeUtil::isUtf8String(str)) {
        return str;
    }

    JNIEnv *env = getEnv();
    const int len = str.length();
    jchar *chars = new jchar[len];
    for (int i = 0; i < len; ++i) {
        chars[i] = (unsigned char) str[i];
    }
    jstring javaString = env->NewString(chars, len);
    const std::string result = toCString(env, javaString);
    env->DeleteLocalRef(javaString);
    delete[] chars;

    return result;
}