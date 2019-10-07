// author : newbiechen
// date : 2019-10-06 19:13
// description : 
//

#include <util/StringUtil.h>

#include "EncodingConverterManager.h"
#include "ASCIIEncodingConverter.h"


EncodingConverterManager &EncodingConverterManager::getInstance() {
    if (sInstance == nullptr) {
        sInstance = new EncodingConverterManager();
    }
    return *sInstance;
}

EncodingConverterManager::EncodingConverterManager() {
    // ascii 转换器
    registerProvider(new ASCIIEncodingConvertProvider());
    // utf-8 转换器
    registerProvider(new ASCIIEncodingConvertProvider());
}

void EncodingConverterManager::registerProvider(std::shared_ptr<EncodingConvertProvider> provider) {
    mProviders.push_back(provider);
}


std::shared_ptr<EncodingConverter> EncodingConverterManager::getEncodingConverter(int code) const {
    std::string name;
    StringUtil::appendNumber(name, code);
    return getEncodingConverter(name);
}

std::shared_ptr<EncodingConverter> EncodingConverterManager::getEncodingConverter(const std::string &name) const {
    for (std::vector<std::shared_ptr<EncodingConvertProvider> >::const_iterator it = mProviders.begin();
         it != mProviders.end(); ++it) {
        if ((*it)->isSupportConverter(name)) {
            return (*it)->createConverter(name);
        }
    }
}

std::shared_ptr<EncodingConverter> EncodingConverterManager::getDefaultConverter() const {
    return getEncodingConverter(ZLEncodingConverter::UTF8);
}