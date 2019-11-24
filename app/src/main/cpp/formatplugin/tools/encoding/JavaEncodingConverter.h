// author : newbiechen
// date : 2019-11-24 00:25
// description : 通过 Java 层实现字符集转换
// TODO：注 Android Native 不允许使用 locale 查找并转换字符集。所以需要通过 Java 层实现。

#ifndef NBREADER_JAVAENCODINGCONVERTER_H
#define NBREADER_JAVAENCODINGCONVERTER_H


#include "EncodingConverter.h"
#include <string>

class JavaEncodingConvertProvider : public EncodingConvertProvider {
public:
    bool isSupportConverter(const std::string &charset) override;

    std::shared_ptr<EncodingConverter> createConverter(const std::string &charset) override;
};

class JavaEncodingConverter : public EncodingConverter {
public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;

    ~JavaEncodingConverter();

private:
    JavaEncodingConverter(const std::string &charset);

private:
    jobject mJavaConverter;
    int mBufferLength;
    jbyteArray mInBuffer;
    jcharArray mOutBuffer;
    jchar *mCppOutBuffer;

    friend class JavaEncodingConvertProvider;
};

#endif //NBREADER_JAVAENCODINGCONVERTER_H
