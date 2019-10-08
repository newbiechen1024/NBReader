// author : newbiechen
// date : 2019-10-07 15:39
// description : 
//

#ifndef NBREADER_DUMMYENCODINGCONVERTER_H
#define NBREADER_DUMMYENCODINGCONVERTER_H

#include <EncodingConverter.h>
#include "EncodingConverter.h"

class ASCIIEncodingConverter : EncodingConverter {
public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;
};

class ASCIIEncodingConvertProvider : EncodingConvertProvider {
public:
    bool isSupportConverter(Charset charset) override;

    std::shared_ptr<EncodingConverter> createConverter(Charset charset) override;
};

#endif //NBREADER_DUMMYENCODINGCONVERTER_H
