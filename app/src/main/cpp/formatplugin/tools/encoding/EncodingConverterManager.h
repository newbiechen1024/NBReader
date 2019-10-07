// author : newbiechen
// date : 2019-10-06 19:13
// description : 编码转换管理器
//

#ifndef NBREADER_ENCODINGCONVERTERMANAGER_H
#define NBREADER_ENCODINGCONVERTERMANAGER_H

#include <vector>
#include <memory>
#include "EncodingConverter.h"

class EncodingConverterManager {
public:
    static EncodingConverterManager &getInstance();

    static void deleteInstance() {
        if (sInstance != nullptr) {
            delete sInstance;
        }
    }

    std::shared_ptr<EncodingConverter> getEncodingConverter(const std::string &name) const;

    std::shared_ptr<EncodingConverter> getEncodingConverter(int code) const;

    std::shared_ptr<EncodingConverter> getDefaultConverter() const;

    void registerProvider(std::shared_ptr<EncodingConvertProvider> provider);

private:
    static EncodingConverterManager *sInstance;

    std::vector<std::shared_ptr<EncodingConvertProvider>> mProviders;

    EncodingConverterManager();

    ~EncodingConverterManager() {
    }
};


#endif //NBREADER_ENCODINGCONVERTERMANAGER_H
