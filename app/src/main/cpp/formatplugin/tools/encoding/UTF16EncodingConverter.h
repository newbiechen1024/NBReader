// author : newbiechen
// date : 2019-10-14 19:33
// description : 
//

#ifndef NBREADER_UTF16ENCODINGCONVERTER_H
#define NBREADER_UTF16ENCODINGCONVERTER_H


#include "EncodingConverter.h"
#include "Charset.h"
#include <memory>

class UTF16EncodingConvertProvider : public EncodingConvertProvider {
public:
    bool isSupportConverter(Charset charset) override;

    std::shared_ptr<EncodingConverter> createConverter(Charset charset) override;
};

class UTF16EncodingConverter : public EncodingConverter {

public:
    void convert(std::string &dst, const char *srcStart, const char *srcEnd) override;

    void reset() override;

protected:
    UTF16EncodingConverter() {
    }

    virtual UnicodeUtil::Ucs2Char unicode2Char(unsigned char c0, unsigned char c1) = 0;

private:
    bool hasStoredChar;
    unsigned char mStoredChar;
};

class UTF16BEEncodingConverter : public UTF16EncodingConverter {
public:
    UTF16BEEncodingConverter() {
    }

protected:

    UnicodeUtil::Ucs2Char unicode2Char(unsigned char c0, unsigned char c1) override;
};

class UTF16LEEncodingConverter : public UTF16EncodingConverter {
public:
    UTF16LEEncodingConverter() {
    }

protected:
    UnicodeUtil::Ucs2Char unicode2Char(unsigned char c0, unsigned char c1) override;
};

#endif //NBREADER_UTF16ENCODINGCONVERTER_H
