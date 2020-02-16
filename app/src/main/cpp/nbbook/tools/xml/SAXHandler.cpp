// author : newbiechen
// date : 2020-02-15 15:16
// description : 
//

#include "SAXHandler.h"

Attributes::Attributes(const char **data) : data(data) {
    // 计算元素的长度
    length = sizeof(data) / sizeof(char *);
}

std::string Attributes::getKey(int index) const {
    if (index >= length) {
        return std::string();
    }

    return std::string(*(data + index * 2));
}

std::string Attributes::getValue(int index) const {
    if (index >= length) {
        return std::string();
    }

    return std::string(*(data + index * 2 + 1));
}

std::string Attributes::getValue(std::string &key) const {
    const char *value = nullptr;
    for (int i = 0; i < length; ++i) {
        value = *(data + i);
        if (strcmp(value, key.c_str())) {
            return getValue(i);
        }
    }
    return std::string();
}