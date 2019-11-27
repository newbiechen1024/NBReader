// author : newbiechen
// date : 2019-10-17 16:08
// description : 
//

#include <filesystem/asset/AssetManager.h>
#include <filesystem/FileSystem.h>
#include <util/Logger.h>
#include <include/uchardet/uchardet.h>
#include <util/StringUtil.h>
#include "LangDetector.h"
#include "LangUtil.h"
#include "LangMatcher.h"
#include "StatisticsNativeReader.h"

// 根据 https://github.com/freedesktop/uchardet 做的映射表
// 根据某些语言专属的 charset 推测出该 charset 对应的语言。如果没有则需要进行语言匹配
// TODO：现在我只写了中文
std::map<std::string, std::string> LangDetector::CHARSET_LANG_MAP = {
        // 中文
        {"big5",    "zh"},
        {"gb18030", "zh"}
};

// 初始化 LangInfo 结构体
LangDetector::LangInfo::LangInfo(const std::string &lang, const std::string &encoding) : lang(lang),
                                                                                         encoding(
                                                                                                 encoding) {
}

LangDetector::LangDetector() : isInitialize(false) {
}

LangDetector::~LangDetector() {
}

static std::string findEncodingInternal(const char *buffer, std::size_t length) {
    uchardet_t handle = uchardet_new();

    int retval = uchardet_handle_data(handle, buffer, length);

    // TODO:需要进行错误处理。
    if (retval != 0) {
        fprintf(stderr,
                "uchardet: handle data error.\n");
        exit(1);
    }

    uchardet_data_end(handle);

    // 生成的是大写 encoding
    std::string encoding(uchardet_get_charset(handle));

    uchardet_delete(handle);

    return encoding.empty() ? Charset::ASCII : encoding;
}


std::shared_ptr<LangDetector::LangInfo>
LangDetector::findLanguage(const char *buffer, std::size_t length) {
    std::string encoding = findEncodingInternal(buffer, length);
    // 需要将得到的编码转化为小写匹配
    StringUtil::asciiToLowerInline(encoding);

    auto it = CHARSET_LANG_MAP.find(encoding);

    // 首先从 charset 和 lang 映射表中查找，如果存在则直接返回
    if (it != CHARSET_LANG_MAP.end()) {
        return std::make_shared<LangInfo>(it->second, it->first);
    }

    return findLanguageWithEncoding(encoding, buffer, length);
}

void LangDetector::initLangMatchers() {
    if (isInitialize) {
        return;
    }

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

    isInitialize = true;
}

std::shared_ptr<LangDetector::LangInfo>
LangDetector::findLanguageWithEncoding(const std::string &encoding, const char *buffer,
                                       size_t length) {

    // 初始化语言匹配器
    // 由于 init 会占用效率，所以移到这里来了
    initLangMatchers();

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