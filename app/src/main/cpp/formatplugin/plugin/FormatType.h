// author : newbiechen
// date : 2019-09-19 16:15
// description : 
//

#ifndef NBREADER_FORMATTYPE_H
#define NBREADER_FORMATTYPE_H

#include <string>

const std::string FORMAT_TYPE_TXT = "txt";
const std::string FORMAT_TYPE_EPUB = "epub";

enum class FormatType {
    TXT,
    EPUB
};

inline std::string formatTypeToStr(FormatType &type) {
    std::string str;
    switch (type) {
        case FormatType::TXT:
            return FORMAT_TYPE_TXT;
        case FormatType::EPUB:
            return FORMAT_TYPE_EPUB;
    }
    return str;
}

inline FormatType strToFormatType(std::string &name) {
    if (name == FORMAT_TYPE_TXT) {
        return FormatType::TXT;
    } else if (name == FORMAT_TYPE_EPUB) {
        return FormatType::EPUB;
    }
    return FormatType::TXT;
}

#endif //NBREADER_FORMATTYPE_H
