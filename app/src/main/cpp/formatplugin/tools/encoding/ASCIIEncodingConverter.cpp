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

bool ASCIIEncodingConvertProvider::isSupportConverter(const std::string &encoding) {
    return false;
}

std::shared_ptr<EncodingConverter> ASCIIEncodingConvertProvider::createConverter(const std::string &encoding) {
    return std::make_shared<ASCIIEncodingConverter>();
}