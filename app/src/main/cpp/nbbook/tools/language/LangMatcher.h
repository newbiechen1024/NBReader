// author : newbiechen
// date : 2019-10-17 18:20
// description : 语言匹配器
//

#ifndef NBREADER_LANGMATCHER_H
#define NBREADER_LANGMATCHER_H


#include "LangDetector.h"
#include "StatisticsTag.h"
#include <memory>

class LangMatcher {
public:
    LangMatcher(std::shared_ptr<LangDetector::LangInfo> langInfo);

    virtual ~LangMatcher();

public:
    std::shared_ptr<LangDetector::LangInfo> getLangInfo() const {
        return mLangInfo;
    }

private:
    std::shared_ptr<LangDetector::LangInfo> mLangInfo;
};

/**
 * Statistic 标签匹配器
 */
class StatisticMatcher : public LangMatcher {
public:
    StatisticMatcher(const std::string &assetFilePath,
                     std::shared_ptr<LangDetector::LangInfo> info);

    ~StatisticMatcher();

    int getCharSequenceLength() const;

    // 获取两个 Statistic 的匹配程度
    int criterion(const StatisticsTag &otherStatistics) const;

private:
    int correlation(const StatisticsTag &candidate, const StatisticsTag &pattern) const;

private:
    std::shared_ptr<XMLStatisticsTag> mStatisticsTag;
};

#endif //NBREADER_LANGMATCHER_H
