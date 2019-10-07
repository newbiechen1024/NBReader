// author : newbiechen
// date : 2019-09-24 14:51
// description : 
//

#include <tools/encoding/EncodingConverterManager.h>
#include "EncodingTextReader.h"

EncodingTextReader::EncodingTextReader(const std::string &encoding) {
    EncodingConverterManager &manager = EncodingConverterManager::getInstance();
    // 根据 encoding 获取到 converter
    std::shared_ptr<EncodingConverter> converter = manager.getEncodingConverter(encoding);
    // 如果 encoding 对应的 converter 不存在，则使用默认的 converter
    if (converter == nullptr) {
        mConverter = manager.getDefaultConverter();
    }
}