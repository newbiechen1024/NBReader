// author : newbiechen
// date : 2019-10-07 15:39
// description : 
//

#include "ASCIIEncodingConverter.h"

void ASCIIEncodingConverter::convert(std::string &dst, const char *srcStart, const char *srcEnd) {
    dst.append(srcStart, srcEnd - srcStart);
}

void ASCIIEncodingConverter::reset() {

}

bool ASCIIEncodingConvertProvider::isSupportConverter(Charset charset) {
    return charset == Charset::ASCII;
}

std::shared_ptr<EncodingConverter> ASCIIEncodingConvertProvider::createConverter(Charset charset) {
    std::shared_ptr<ASCIIEncodingConverter> converter(new ASCIIEncodingConverter());
    return std::dynamic_pointer_cast<EncodingConverter>(converter);
}