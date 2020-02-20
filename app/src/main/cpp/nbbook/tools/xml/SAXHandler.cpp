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

std::string Attributes::getValue(const std::string &key) const {
    const char *value = nullptr;
    for (int i = 0; i < length; ++i) {
        value = *(data + i);
        if (strcmp(value, key.c_str())) {
            return getValue(i);
        }
    }
    return std::string();
}

std::map<std::string, std::string> Attributes::getAttributeMap() {
    std::map<std::string, std::string> map;
    size_t offset = 0;
    while (*(data + offset) != 0) {
        std::string key = *(data + offset);
        ++offset;
        if (*(data + offset) == 0) {
            break;
        }
        map[key] = *(data + offset);
        ++offset;
    }
    return map;
}