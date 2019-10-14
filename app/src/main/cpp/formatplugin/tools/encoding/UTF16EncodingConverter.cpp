// author : newbiechen
// date : 2019-10-14 19:33
// description : 
//

#include "UTF16EncodingConverter.h"

bool UTF16EncodingConvertProvider::isSupportConverter(Charset charset) {
    return charset == Charset::UTF16 || charset == Charset::UTF16BE;
}

std::shared_ptr<EncodingConverter> UTF16EncodingConvertProvider::createConverter(Charset charset) {
    if (Charset::UTF16 == charset) {
        return std::make_shared<UTF16LEEncodingConverter>();
    } else {
        return std::make_shared<UTF16BEEncodingConverter>();
    }
}


void UTF16EncodingConverter::convert(std::string &dst, const char *srcStart, const char *srcEnd) {
    if (srcStart >= srcEnd) {
        return;
    }
    char buffer[3];
    if (hasStoredChar) {
        dst.append(buffer, UnicodeUtil::unicode2ToUtf8(buffer, unicode2Char(mStoredChar, *srcStart)));
        ++srcStart;
        hasStoredChar = false;
    }
    if ((srcEnd - srcStart) % 2 == 1) {
        --srcEnd;
        mStoredChar = (unsigned char) *srcEnd;
        hasStoredChar = true;
    }
    for (; srcStart != srcEnd; srcStart += 2) {
        dst.append(buffer, UnicodeUtil::unicode2ToUtf8(buffer, unicode2Char(*srcStart, *(srcStart + 1))));
    }
}

void UTF16EncodingConverter::reset() {
    hasStoredChar = false;
}

UnicodeUtil::Unicode2Char UTF16LEEncodingConverter::unicode2Char(unsigned char c0, unsigned char c1) {
    return c0 + (((UnicodeUtil::Unicode2Char) c1) << 8);

}

UnicodeUtil::Unicode2Char UTF16BEEncodingConverter::unicode2Char(unsigned char c0, unsigned char c1) {
    return c1 + (((UnicodeUtil::Unicode2Char) c0) << 8);
}


