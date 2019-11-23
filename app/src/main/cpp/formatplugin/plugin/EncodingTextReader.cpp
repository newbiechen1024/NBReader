// author : newbiechen
// date : 2019-09-24 14:51
// description : 
//

#include <tools/encoding/EncodingConverterManager.h>
#include "EncodingTextReader.h"
#include <locale>

EncodingTextReader::EncodingTextReader(const std::string & charset) : mConverter(nullptr) {

    EncodingConverterManager &manager = EncodingConverterManager::getInstance();
    // 根据 encoding 获取到 converter
    mConverter = manager.getEncodingConverter(charset);
    // 如果 encoding 对应的 converter 不存在，则使用默认的 converter
    if (mConverter == nullptr) {
        mConverter = manager.getDefaultConverter();
    }
}