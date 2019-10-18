// author : newbiechen
// date : 2019-10-07 18:55
// description : 字符集枚举

#ifndef NBREADER_CHARSET_H
#define NBREADER_CHARSET_H

#include <string>
#include <util/UnicodeUtil.h>

const std::string CHARSET_ASCII = "us-ascii";
const std::string CHARSET_UTF8 = "utf-8";
const std::string CHARSET_UTF16 = "utf-16";
const std::string CHARSET_UTF16BE = "utf-16be";
const std::string CHARSET_ISO8859 = "iso-8859-1";
const std::string CHARSET_WINDOWS1252 = "windows-1252";

enum class Charset {
    NONE = 0,
    ASCII,
    UTF8,
    UTF16,
    UTF16BE,
    ISO8859,
    WINDOWS1252,
};

inline Charset strToCharset(const std::string &str) {
    if (str.empty()) {
        return Charset::NONE;
    }

    std::string lowerStr = UnicodeUtil::toLower(str);

    if (lowerStr == CHARSET_ASCII) {
        return Charset::ASCII;
    } else if (lowerStr == CHARSET_UTF8) {
        return Charset::UTF8;

    } else if (lowerStr == CHARSET_UTF16) {
        return Charset::UTF16;

    } else if (lowerStr == CHARSET_UTF16BE) {
        return Charset::UTF16BE;

    } else if (lowerStr == CHARSET_ISO8859) {
        return Charset::ISO8859;

    } else if (lowerStr == CHARSET_WINDOWS1252) {
        return Charset::WINDOWS1252;
    } else {
        // 默认返回 UTF-8
        return Charset::UTF8;
    }
}

inline std::string charsetToStr(Charset charset) {
    switch (charset) {
        case Charset::ASCII:
            return CHARSET_ASCII;
        case Charset::UTF8:
            return CHARSET_UTF8;
        case Charset::UTF16:
            return CHARSET_UTF16;
        case Charset::UTF16BE:
            return CHARSET_UTF16BE;
        case Charset::ISO8859:
            return CHARSET_ISO8859;
        case Charset::WINDOWS1252:
            return CHARSET_WINDOWS1252;
        default:
            return "";
    }
}


#endif //NBREADER_CHARSET_H
