// author : newbiechen
// date : 2019-10-17 16:08
// description : 
//

#include <filesystem/asset/AssetManager.h>
#include <filesystem/FileSystem.h>
#include <util/Logger.h>
#include "LangDetector.h"
#include "LangUtil.h"
#include "LangMatcher.h"
#include "StatisticsNativeReader.h"

// 初始化 LangInfo 结构体
LangDetector::LangInfo::LangInfo(const std::string &lang, const std::string &encoding) : lang(lang),
                                                                                         encoding(
                                                                                                 encoding) {
}

LangDetector::LangDetector() {
    // 获取文件目录路径
    std::string patternDirPath = LangUtil::getPatternDirectoryFromAsset();
    auto patternFileNames = AssetManager::getInstance().list(patternDirPath, false);
    if (!patternFileNames->empty()) {
        for (std::string &fileName: (*patternFileNames)) {
            const int index = fileName.find('_');
            std::string filePath = patternDirPath + FileSystem::separator + fileName;
            if (index != -1) {
                const std::string language = fileName.substr(0, index);
                const std::string encoding = fileName.substr(index + 1);

                std::shared_ptr<StatisticMatcher> matcher = std::make_shared<StatisticMatcher>(
                        filePath, std::make_shared<LangInfo>(language, encoding));
                mMatchers.push_back(matcher);
            }
        }
    }
}

LangDetector::~LangDetector() {
}

static std::string findEncodingInternal(const unsigned char *buffer, std::size_t length) {
    if (buffer[0] == 0xFE && buffer[1] == 0xFF) {
        return Charset::UTF16BE;
    }
    if (buffer[0] == 0xFF && buffer[1] == 0xFE) {
        return Charset::UTF16;
    }

    bool ascii = true;
    const unsigned char *end = buffer + length;
    int utf8count = 0;
    for (const unsigned char *ptr = buffer; ptr < end; ++ptr) {
        if (utf8count > 0) {
            if ((*ptr & 0xc0) != 0x80) {
                return std::string();
            }
            --utf8count;
        } else if ((*ptr & 0x80) == 0) {
        } else if ((*ptr & 0xe0) == 0xc0) {
            ascii = false;
            utf8count = 1;
        } else if ((*ptr & 0xf0) == 0xe0) {
            ascii = false;
            utf8count = 2;
        } else if ((*ptr & 0xf8) == 0xf0) {
            ascii = false;
            utf8count = 3;
        } else {
            return std::string();
        }
    }
    return ascii ? Charset::ASCII : Charset::UTF8;
}


std::shared_ptr<LangDetector::LangInfo>
LangDetector::findLanguage(const char *buffer, std::size_t length) {
    std::string naiveLang;
    if ((unsigned char) buffer[0] == 0xFE &&
        (unsigned char) buffer[1] == 0xFF) {
        naiveLang = Charset::UTF16BE;
    } else if ((unsigned char) buffer[0] == 0xFF &&
               (unsigned char) buffer[1] == 0xFE) {
        naiveLang = Charset::UTF16;
    } else {
        naiveLang = findEncodingInternal((const unsigned char *) buffer, length);
    }
    return findLanguageWithEncoding(naiveLang, buffer, length);
}

std::shared_ptr<LangDetector::LangInfo>
LangDetector::findLanguageWithEncoding(const std::string &encoding, const char *buffer,
                                       size_t length) {
    int matchingCriterion = 0;
    std::shared_ptr<LangInfo> langInfo = nullptr;
    std::map<int, std::shared_ptr<NativeStatisticsTag>> statisticsMap;
    StatisticsNativeReader nativeStatisticsReader("\r\n ");

    // 遍历所有 Matcher 匹配器
    for (auto matcher : mMatchers) {
        // 如果 encoding 类型为空，或者 matcher 的 encoding 类型与当前 encoding 类型不同
        if (!encoding.empty() && matcher->getLangInfo()->encoding != encoding) {
            continue;
        }

        // 获取 matcher 指定的一个字的长度
        const int charSequenceLength = matcher->getCharSequenceLength();
        // 根据长度从 map 中获取解析对应字长度的 NativeStatisticsTag
        std::shared_ptr<NativeStatisticsTag> stat = statisticsMap[charSequenceLength];

        if (stat == nullptr) {
            stat = std::make_shared<NativeStatisticsTag>();
            // 使用 Native 解析器读取 buffer 信息并填充到 NativeStatisticsTag 中
            nativeStatisticsReader.readNativeStatistics(
                    buffer, length, charSequenceLength, *stat
            );

            statisticsMap[charSequenceLength] = stat;
        }

        const int criterion = matcher->criterion(*stat);
        if (criterion > matchingCriterion) {
            langInfo = matcher->getLangInfo();
            matchingCriterion = criterion;
        }
    }
    return langInfo;
}