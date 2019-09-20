// author : newbiechen
// date : 2019-09-19 16:15
// description : 
//

#ifndef NBREADER_FORMATTYPE_H
#define NBREADER_FORMATTYPE_H

#include <string>

const std::string FORMAT_TYPE_TXT = "txt";
const std::string FORMAT_TYPE_EPUB = "epub";

enum FormatType {
    TXT,
    EPUB
};

std::string formatTypeToStr(FormatType &type) {
    std::string str;
    switch (type) {
        case TXT:
            return FORMAT_TYPE_TXT;
        case EPUB:
            return FORMAT_TYPE_EPUB;
    }
    return str;
}

FormatType strToFormatType(std::string &name) {
    if (name == FORMAT_TYPE_TXT) {
        return TXT;
    } else if (name == FORMAT_TYPE_EPUB) {
        return EPUB;
    }
    return TXT;
}

#endif //NBREADER_FORMATTYPE_H
