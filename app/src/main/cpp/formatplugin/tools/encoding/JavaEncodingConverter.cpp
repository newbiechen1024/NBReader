// author : newbiechen
// date : 2019-11-24 00:25
// description : 
//

#include "JavaEncodingConverter.h"

bool JavaEncodingConvertProvider::isSupportConverter(const std::string &charset) {
    if (charset.empty()) {
        return false;
    }

    JNIEnv *env = AndroidUtil::getEnv();
    // 将 charset 转换成 jstring
    jstring jCharset = AndroidUtil::toJString(env, charset);
    // 传送给 Java 层判断是否支持 charset
    jboolean result = AndroidUtil::StaticMethod_EncodingConverter_isEncodingSupport->call(
            jCharset);

    env->DeleteLocalRef(jCharset);
    return result != 0;
}

std::shared_ptr<EncodingConverter> JavaEncodingConvertProvider::createConverter(
        const std::string &charset) {
    // TODO：没有处理 charset 如果不支持的情况

    std::shared_ptr<JavaEncodingConverter> converter(new JavaEncodingConverter(charset));
    return converter;
}

JavaEncodingConverter::JavaEncodingConverter(const std::string &charset) {
    JNIEnv *env = AndroidUtil::getEnv();
    jstring jCharset = AndroidUtil::toJString(env, charset);

    mJavaConverter = AndroidUtil::StaticMethod_EncodingConverter_createEncodingConverter->call(
            jCharset);

    env->DeleteLocalRef(jCharset);

    mBufferLength = 32768;

    mInBuffer = env->NewByteArray(mBufferLength);
    mOutBuffer = env->NewCharArray(mBufferLength);

    mCppOutBuffer = new jchar[mBufferLength];
}

JavaEncodingConverter::~JavaEncodingConverter() {
    JNIEnv *env = AndroidUtil::getEnv();

    delete[] mCppOutBuffer;
    env->DeleteLocalRef(mOutBuffer);
    env->DeleteLocalRef(mInBuffer);
    env->DeleteLocalRef(mJavaConverter);
}

void JavaEncodingConverter::convert(std::string &dst, const char *srcStart, const char *srcEnd) {
    JNIEnv *env = AndroidUtil::getEnv();
    const int srcLen = srcEnd - srcStart;

    if (srcLen > mBufferLength) {
        delete[] mCppOutBuffer;
        env->DeleteLocalRef(mOutBuffer);
        env->DeleteLocalRef(mInBuffer);
        mBufferLength = srcLen;
        mInBuffer = env->NewByteArray(mBufferLength);
        mOutBuffer = env->NewCharArray(mBufferLength);
        mCppOutBuffer = new jchar[mBufferLength];
    }

    env->SetByteArrayRegion(mInBuffer, 0, srcLen, (jbyte *) srcStart);
    // 解码生成的数量
    const jint decodedCount = AndroidUtil::Method_EncodingConverter_convert->call(
            mJavaConverter, mInBuffer, 0, srcLen, mOutBuffer
    );
    // 设置 dst 的最大长度
    dst.reserve(dst.length() + decodedCount * 3);
    // 将 java 的 char 转换成 c 的 char
    env->GetCharArrayRegion(mOutBuffer, 0, decodedCount, mCppOutBuffer);
    const jchar *end = mCppOutBuffer + decodedCount;
    char buffer[3];
    // 将 Unicode字符集，转换成 UTF-8
    for (const jchar *ptr = mCppOutBuffer; ptr < end; ++ptr) {
        dst.append(buffer, UnicodeUtil::ucs2ToUtf8(buffer, *ptr));
    }
}

void JavaEncodingConverter::reset() {
    AndroidUtil::Method_EncodingConverter_reset->call(mJavaConverter);
}