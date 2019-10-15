// author : newbiechen
// date : 2019-10-07 15:38
// description : 
//

#ifndef NBREADER_UTF8ENCODINGCONVERTER_H
#define NBREADER_UTF8ENCODINGCONVERTER_H


#include "EncodingConverter.h"
#include <string>
#include <memory>

class UTF8EncodingConvertProvider : public EncodingConvertProvider {
public:
    bool isSupportConverter(Charset charset) override;

    std::shared_ptr<EncodingConverter> createConverter(Charset charset) override;
};

class UTF8EncodingConverter : public EncodingConverter {
public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;

private:
    std::string mBuffer;

    friend class UTF8EncodingConvertProvider;
};

#endif //NBREADER_UTF8ENCODINGCONVERTER_H
