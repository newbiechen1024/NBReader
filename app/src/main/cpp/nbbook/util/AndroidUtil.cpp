//
// Created by 陈广祥 on 2019-09-19.
//

#include "AndroidUtil.h"
#include "UnicodeUtil.h"

JavaVM *AndroidUtil::sJavaVM = 0;

const std::string PKG_NAME = "com/newbiechen/nbreader/";

JavaClass AndroidUtil::Class_String("java/lang/String");

JavaClass AndroidUtil::Class_Locale("java/util/Locale");

JavaClass AndroidUtil::Class_NativeFormatPlugin(
        PKG_NAME + "ui/component/book/plugin/NativeFormatPlugin");

JavaClass AndroidUtil::Class_BoolPluginManager(
        PKG_NAME + "ui/component/book/plugin/BookPluginManager");

JavaClass AndroidUtil::Class_BookEntity(PKG_NAME + "data/entity/BookEntity");

JavaClass AndroidUtil::Class_TextChapter(
        PKG_NAME + "ui/component/book/text/entity/TextChapter"
);

std::shared_ptr<StaticObjectMethod> AndroidUtil::StaticMethod_Locale_getDefault;

std::shared_ptr<StringMethod> AndroidUtil::Method_Locale_getLanguage;

std::shared_ptr<StringMethod> AndroidUtil::Method_String_toLowerCase;
std::shared_ptr<StringMethod> AndroidUtil::Method_String_toUpperCase;

// Book
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getTitle;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getUrl;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getEncoding;
std::shared_ptr<StringMethod> AndroidUtil::Method_Book_getLang;

// TextChapter
std::shared_ptr<JavaConstructor> AndroidUtil::Constructor_TextChapter;
std::shared_ptr<StringMethod> AndroidUtil::Method_TextChapter_getUrl;
std::shared_ptr<StringMethod> AndroidUtil::Method_TextChapter_getTitle;
std::shared_ptr<IntMethod> AndroidUtil::Method_TextChapter_getStartIndex;
std::shared_ptr<IntMethod> AndroidUtil::Method_TextChapter_getEndIndex;


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

    Method_Locale_getLanguage = std::make_shared<StringMethod>(Class_Locale, "getLanguage", "()");

    // string
    Method_String_toLowerCase = std::make_shared<StringMethod>(Class_String, "toLowerCase", "()");
    Method_String_toUpperCase = std::make_shared<StringMethod>(Class_String, "toUpperCase", "()");

    /*Book*/
    Method_Book_getTitle = std::make_shared<StringMethod>(Class_BookEntity, "getTitle", "()");
    Method_Book_getUrl = std::make_shared<StringMethod>(Class_BookEntity, "getUrl", "()");
    Method_Book_getEncoding = std::make_shared<StringMethod>(Class_BookEntity, "getEncoding", "()");
    Method_Book_getLang = std::make_shared<StringMethod>(Class_BookEntity, "getLang", "()");

    Method_TextChapter_getUrl = std::make_shared<StringMethod>(Class_TextChapter, "getUrl", "()");
    Method_TextChapter_getTitle = std::make_shared<StringMethod>(Class_TextChapter, "getTitle",
                                                                 "()");
    Method_TextChapter_getStartIndex = std::make_shared<IntMethod>(Class_TextChapter,
                                                                    "getStartIndex", "()");
    Method_TextChapter_getEndIndex = std::make_shared<IntMethod>(Class_TextChapter, "getEndIndex",
                                                                  "()");

    // 参数：string、string、int、int
    Constructor_TextChapter = std::make_shared<JavaConstructor>(Class_TextChapter,
                                                                "(Ljava/lang/String;Ljava/lang/String;II)V");
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

/*
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
}*/
