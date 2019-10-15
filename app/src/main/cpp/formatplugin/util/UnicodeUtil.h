// author : newbiechen
// date : 2019-09-24 15:51
// description : 
//

#ifndef NBREADER_UNICODEUTIL_H
#define NBREADER_UNICODEUTIL_H

#include <stdint.h>
#include <string>
#include <vector>
#include "JNIEnvelope.h"
#include "AndroidUtil.h"

class UnicodeUtil {

private:
    UnicodeUtil();

public:
    typedef uint16_t Unicode2Char;
    typedef uint32_t Unicode4Char;

    typedef std::vector<Unicode2Char> Unicode2String;
    typedef std::vector<Unicode4Char> Unicode4String;

    enum Breakable {
        NO_BREAKABLE,
        BREAKABLE_BEFORE,
        BREAKABLE_AFTER
    };

    static bool isUtf8String(const char *str, int len);

    static bool isUtf8String(const std::string &str);

    static void cleanUtf8String(std::string &str);

    static int utf8Length(const char *str, int len);

    static int utf8Length(const std::string &str);

    static int length(const char *str, int utf8Length);

    static int length(const std::string &str, int utf8Length);

    static void utf8ToUnicode4(Unicode4String &to, const char *from, int length, int toLength = -1);

    static void utf8ToUnicode4(Unicode4String &to, const std::string &from, int toLength = -1);

    static void utf8ToUnicode2(Unicode2String &to, const char *from, int length, int toLength = -1);

    static void utf8ToUnicode2(Unicode2String &to, const std::string &from, int toLength = -1);

    static std::size_t firstChar(Unicode4Char &ch, const char *utf8String);

    static std::size_t firstChar(Unicode4Char &ch, const std::string &utf8String);

    static std::size_t lastChar(Unicode4Char &ch, const char *utf8String);

    static std::size_t lastChar(Unicode4Char &ch, const std::string &utf8String);

    static void unicode4ToUtf8(std::string &to, const Unicode4String &from, int toLength = -1);

    static int unicode4ToUtf8(char *to, Unicode4Char ch);

    static void unicode2ToUtf8(std::string &to, const Unicode2String &from, int toLength = -1);

    static int unicode2ToUtf8(char *to, Unicode2Char ch);

    static bool isSpace(Unicode4Char ch);

    static Breakable isBreakable(Unicode4Char ch);

    static std::string toLower(const std::string &utf8String);

    static std::string toUpper(const std::string &utf8String);

    static void utf8Trim(std::string &utf8String);

    static bool isNBSpace(Unicode4Char ch) {
        return ch == 160;
    }
};

#endif //NBREADER_UNICODEUTIL_H
