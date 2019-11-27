// author : newbiechen
// date : 2019-11-25 23:27
// description : 
//

#include <util/Logger.h>
#include <util/StringUtil.h>
#include "TxtChapterReader.h"

static const size_t BUFFER_SIZE = 256 * 1024;

TxtChapterReader::TxtChapterReader(const std::string &lang, const std::string &charset)
        : EncodingTextReader(charset) {
    mRegexCode = 0;
    initRegexStr(lang, charset);
}

TxtChapterReader::~TxtChapterReader() {
    regfree(&mRegex);
}

void testRegexStr(InputStream &inputStream) {
    // 如果输入流打开失败
    if (!inputStream.open()) {
        return;
    }

    regex_t re;
    regmatch_t subs[4];
    char pattern[] = "^(.{0,8})(\xb5\xda)([0-9\xc1\xe3\xd2\xbb\xb6\xfe\xc1\xbd\xc8\xfd\xcb\xc4\xce\xe5\xc1\xf9\xc6\xdf\xb0\xcb\xbe\xc5\xca\xae\xb0\xd9\xc7\xa7\xcd\xf2\xd2\xbc\xb7\xa1\xc8\xfe\xcb\xc1\xce\xe9\xc2\xbd\xc6\xe2\xb0\xc6\xbe\xc1\xca\xb0\xb0\xdb\xc7\xaa]{1,10})([\xd5\xc2\xbd\xda\xbb\xd8\xbc\xaf\xbe\xed])(.{0,30})$";

    int err = regcomp(&re, pattern, REG_EXTENDED | REG_NEWLINE);

    if (err) {
        Logger::i("TxtChapterReader", "regex error ");
        return;
    }

    const size_t BUFFER_SIZE = 256 * 1024;
    char *buffer = new char[BUFFER_SIZE];
    size_t length;
    size_t count = 0;

    do {
        // 读取文本
        length = inputStream.read(buffer, BUFFER_SIZE);

        // 获取起始点和终止点
        char *start = buffer;
        char *end = buffer + length;

        const char *ptr = buffer;
        // 匹配所有模式字串
        while (start != end) {
            memset(subs, 0, sizeof(subs));
            err = regexec(&re, ptr, (size_t) 4, subs, 0);

            if (err == REG_NOMATCH) {
                Logger::i("TxtChapterReader", "no match");
                break;
            } else if (err) {
                char errbuf[1024];
                regerror(err, &re, errbuf, sizeof(errbuf));
                Logger::e("TxtChapterReader", errbuf);
                break;
            }

            int len = subs[0].rm_eo - subs[0].rm_so;

            ptr = ptr + subs[0].rm_so + len;

            count++;
        }
    } while (length == BUFFER_SIZE);

    std::string log = "result:";
    StringUtil::appendNumber(log, count);
    Logger::e("TxtChapterReader", log);

    delete[]buffer;
}

void TxtChapterReader::initRegexStr(const std::string &lang, const std::string &charset) {
    JNIEnv *env = AndroidUtil::getEnv();
    // 将 charset 转换成 jstring
    jstring jLang = AndroidUtil::toJString(env, lang);
    jstring jCharset = AndroidUtil::toJString(env, charset);

    // 传送给 Java 层判断是否支持 charset
    jobject jChapterDetector = AndroidUtil::StaticMethod_ChapterDetector_createChapterDetector->call(
            jLang);

    std::string regexStr = AndroidUtil::Method_ChapterDetector_getRegexStr->callForCppString(
            jChapterDetector,
            jCharset);

    Logger::i("TxtChapterReader", "regex string:" + regexStr);

    // 初始化模式匹配器
    mRegexCode = regcomp(&mRegex, regexStr.c_str(), REG_EXTENDED | REG_NEWLINE);

    env->DeleteLocalRef(jCharset);
    env->DeleteLocalRef(jLang);
}


void TxtChapterReader::readDocument(InputStream &inputStream) {
    testRegexStr(inputStream);

    /*if (mRegexCode != 0) {
        return;
    }

    Logger::i("TxtChapterReader", "readDocument:start");

    // 如果输入流打开失败
    if (!inputStream.open()) {
        return;
    }

    char *buffer = new char[BUFFER_SIZE];
    size_t length;

    regmatch_t matchArr[1024];
    int errCode;

    // 循环获取数据
    do {
        // 读取文本
        length = inputStream.read(buffer, BUFFER_SIZE);

        // 获取起始点和终止点
        char *start = buffer;
        char *end = buffer + length;

        char *ptr = buffer;

        // 对 buffer 进行文字匹配
        while (start != end) {
            // 进行匹配
            errCode = regexec(&mRegex, ptr, 1024, matchArr, 0);

            // 匹配出问题了
            if (errCode == REG_NOMATCH) {
                Logger::i("TxtChapterReader", "no match");
                break;
            } else if (errCode) {
                char errbuf[1024];
                regerror(errCode, &mRegex, errbuf, sizeof(errbuf));
                Logger::e("TxtChapterReader", errbuf);
                break;
            }

            int len = matchArr[0].rm_eo - matchArr[0].rm_so;
            char *chapterStart = ptr + matchArr[0].rm_so;
            char *chapterEnd = chapterStart + len;

            std::string value(chapterStart, chapterEnd);
            Logger::i("TxtChapterReader", "match:" + value);
            ptr = ptr + matchArr[0].rm_so + len;
        }

    } while (length == BUFFER_SIZE);*/

    Logger::i("TxtChapterReader", "readDocument:end");
}