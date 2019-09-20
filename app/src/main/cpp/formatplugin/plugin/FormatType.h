// author : newbiechen
// date : 2019-09-19 16:15
// description : 
//

#ifndef NBREADER_FORMATTYPE_H
#define NBREADER_FORMATTYPE_H

#include <string>

enum FormatType {
    TXT,
    EPUB
};

std::string formatTypeToStr(FormatType &type) {
    std::string str;
    switch (type) {
        case TXT:
            return "txt";
        case EPUB:
            return "epub";
    }
    return str;
}

#endif //NBREADER_FORMATTYPE_H
