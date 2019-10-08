// author : newbiechen
// date : 2019-10-06 19:12
// description :
// 1. EncodingConverter:编码转换器接口
// 2. EncodingConvertProvider: 编码转换提供器

#ifndef NBREADER_ENCODINGCOVERTER_H
#define NBREADER_ENCODINGCOVERTER_H

#include <string>
#include <memory>
#include "Charset.h"

class EncodingConverter {
public:
    static const std::string ASCII;
    static const std::string UTF8;
    static const std::string UTF16;
    static const std::string UTF16BE;

protected:
    EncodingConverter() {}

public:
    virtual ~EncodingConverter(){}

    void convert(std::string &dst, const std::string &src);

    virtual void convert(std::string &dst, const char *srcStart, const char *srcEnd) = 0;
    // 重置
    virtual void reset() = 0;

// 禁止复制
private:
    EncodingConverter(const EncodingConverter &);

    EncodingConverter &operator=(const EncodingConverter &);
};

class EncodingConvertProvider {
protected:
    EncodingConvertProvider(){}

public:
    virtual ~EncodingConvertProvider(){}

    // 该编码是否支持转换
    virtual bool isSupportConverter(Charset charset) = 0;

    // 创建转换器
    virtual std::shared_ptr<EncodingConverter> createConverter(Charset charset) = 0;

    // 禁止复制
private:
    EncodingConvertProvider(const EncodingConvertProvider &);

    const EncodingConvertProvider &operator=(const EncodingConvertProvider &);
};

#endif //NBREADER_ENCODINGCOVERTER_H
