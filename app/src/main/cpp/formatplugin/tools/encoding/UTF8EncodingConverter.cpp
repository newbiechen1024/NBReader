// author : newbiechen
// date : 2019-10-07 15:38
// description : 
//

#include <util/UnicodeUtil.h>
#include "UTF8EncodingConverter.h"

void UTF8EncodingConverter::convert(std::string &dst, const char *srcStart, const char *srcEnd) {

    if (mBuffer.size() > 0) {
        const std::size_t len = UnicodeUtil::length(mBuffer, 1);
        if (len < mBuffer.size()) {
            return;
        }
        const std::size_t diff = std::min(len - mBuffer.size(), (std::size_t) (srcEnd - srcStart));
        mBuffer.append(srcStart, diff);
        srcStart += diff;
        if (mBuffer.size() == len) {
            dst += mBuffer;
            mBuffer.clear();
        }
    }

    for (const char *ptr = srcEnd - 1; ptr >= srcEnd - 6 && ptr >= srcStart; --ptr) {
        if ((*ptr & 0xC0) != 0x80) {
            if (UnicodeUtil::length(ptr, 1) > srcEnd - ptr) {
                mBuffer.append(ptr, srcEnd - ptr);
                srcEnd = ptr;
            }
            break;
        }
    }

    dst.append(srcStart, srcEnd - srcStart);
}

void UTF8EncodingConverter::reset() {
    mBuffer.clear();
}

bool UTF8EncodingConvertProvider::isSupportConverter(Charset charset) {
    return charset == Charset::UTF8;
}

std::shared_ptr<EncodingConverter> UTF8EncodingConvertProvider::createConverter(Charset charset) {
    return std::make_shared<UTF8EncodingConverter>();
}
