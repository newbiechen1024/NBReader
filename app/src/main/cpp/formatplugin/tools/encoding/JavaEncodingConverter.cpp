// author : newbiechen
// date : 2019-10-07 15:38
// description : 
//

#include "JavaEncodingConverter.h"

bool JavaEncodingConverterProvider::isSupportConverter(const std::string &encoding) {
    return false;
}

std::shared_ptr<EncodingConverter> JavaEncodingConverterProvider::createConverter(const std::string &encoding) {
    return std::shared_ptr<EncodingConverter>();
}

void JavaEncodingConverter::convert(std::string &dst, const char *srcStart, const char *srcEnd) {

}

void JavaEncodingConverter::reset() {

}
