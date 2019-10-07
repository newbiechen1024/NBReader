// author : newbiechen
// date : 2019-10-06 19:12
// description : 
//

#include "EncodingConverter.h"

void EncodingConverter::convert(std::string &dst, const std::string &src) {
    convert(dst, src.data(), src.data() + src.length());
}