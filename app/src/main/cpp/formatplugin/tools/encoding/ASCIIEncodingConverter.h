// author : newbiechen
// date : 2019-10-07 15:39
// description : 
//

#ifndef NBREADER_DUMMYENCODINGCONVERTER_H
#define NBREADER_DUMMYENCODINGCONVERTER_H

#include "EncodingConverter.h"

class ASCIIEncodingConverter : public EncodingConverter {
public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;
};

class ASCIIEncodingConvertProvider : public EncodingConvertProvider {
public:
    bool isSupportConverter(const std::string & charset) override;

    std::shared_ptr<EncodingConverter> createConverter(const std::string & charset) override;
};

#endif //NBREADER_DUMMYENCODINGCONVERTER_H
