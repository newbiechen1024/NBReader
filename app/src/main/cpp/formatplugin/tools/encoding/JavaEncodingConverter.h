// author : newbiechen
// date : 2019-10-07 15:38
// description : 对于 Java 层的文字的实现
// TODO：由于没有明白为什么一定要交到 Android 上层去实现 Encoding，这部分代码暂时先不实现

#ifndef NBREADER_JAVAENCODINGCONVERTER_H
#define NBREADER_JAVAENCODINGCONVERTER_H

#include "EncodingConverter.h"

class JavaEncodingConverter : EncodingConverter {
public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;
};

class JavaEncodingConverterProvider : EncodingConvertProvider {
public:
    bool isSupportConverter(const std::string &encoding) override;

    std::shared_ptr<EncodingConverter> createConverter(const std::string &encoding) override;
};

#endif //NBREADER_JAVAENCODINGCONVERTER_H
