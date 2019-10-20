// author : newbiechen
// date : 2019-09-24 15:51
// description : 
// TODO:Unicode 处理工具要改了

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
    // 表示 unicode char sequence: 一个字占用的字节数。
    typedef uint16_t Ucs2Char;
    typedef uint32_t Ucs4Char;

    typedef std::vector<Ucs2Char> Ucs2String;
    typedef std::vector<Ucs4Char> UcsString;

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

    static void utf8ToUcs4(UcsString &to, const char *from, int length, int toLength = -1);

    static void utf8ToUcs4(UcsString &to, const std::string &from, int toLength = -1);

    static void utf8ToUcs2(Ucs2String &to, const char *from, int length, int toLength = -1);

    static void utf8ToUcs2(Ucs2String &to, const std::string &from, int toLength = -1);

    static std::size_t firstChar(Ucs4Char &ch, const char *utf8String);

    static std::size_t firstChar(Ucs4Char &ch, const std::string &utf8String);

    static std::size_t lastChar(Ucs4Char &ch, const char *utf8String);

    static std::size_t lastChar(Ucs4Char &ch, const std::string &utf8String);

    static void ucs4ToUtf8(std::string &to, const UcsString &from, int toLength = -1);

    static int ucs4ToUtf8(char *to, Ucs4Char ch);

    static void ucs2ToUtf8(std::string &to, const Ucs2String &from, int toLength = -1);

    static int ucs2ToUtf8(char *to, Ucs2Char ch);

    static bool isSpace(Ucs4Char ch);

    static Breakable isBreakable(Ucs4Char ch);

    static std::string toLower(const std::string &utf8String);

    static std::string toUpper(const std::string &utf8String);

    static void utf8Trim(std::string &utf8String);

    static bool isNBSpace(Ucs4Char ch) {
        return ch == 160;
    }
};

#endif //NBREADER_UNICODEUTIL_H
