// author : newbiechen
// date : 2019-10-17 18:20
// description : 
//

#include "LangMatcher.h"


LangMatcher::LangMatcher(std::shared_ptr<LangDetector::LangInfo> info) : mLangInfo(info) {
}

LangMatcher::~LangMatcher() {
}

StatisticMatcher::StatisticMatcher(const std::string &fileName, std::shared_ptr<LangDetector::LangInfo> info)
        : LangMatcher(info) {
    // todo:reader 暂时未实现
    // mStatisticsTag = ZLStatisticsXMLReader().readStatistics(fileName);
}

StatisticMatcher::~StatisticMatcher() {
}

int StatisticMatcher::getCharSequenceLength() const {
    return mStatisticsTag->getCharSequenceSize();
}

static int log10(long long number) {
    int count = 0;
    while (number != 0) {
        number /= 10;
        ++count;
    }
    return count;
}


int StatisticMatcher::correlation(const StatisticsTag &candidate, const StatisticsTag &pattern) const {
    if (&candidate == &pattern) {
        return 1000000;
    }
    const unsigned long long candidateSum = candidate.getVolume();
    const unsigned long long patternSum = pattern.getVolume();
    const unsigned long long candidateSum2 = candidate.getSquaresVolume();
    const unsigned long long patternSum2 = pattern.getSquaresVolume();

    std::shared_ptr<StatisticsItemTag> ptrA = candidate.begin();
    std::shared_ptr<StatisticsItemTag> ptrB = pattern.begin();
    const std::shared_ptr<StatisticsItemTag> endA = candidate.end();
    const std::shared_ptr<StatisticsItemTag> endB = pattern.end();

    std::size_t count = 0;
    long long correlationSum = 0;
    if (*ptrA != *endA)
        while ((*ptrA != *endA) && (*ptrB != *endB)) {
            ++count;
            const int comparison = ptrA->sequence().compareTo(ptrB->sequence());
            if (comparison < 0) {
                ptrA->next();
            } else if (comparison > 0) {
                ptrB->next();
            } else {
                correlationSum += ptrA->frequency() * ptrB->frequency();
                ptrA->next();
                ptrB->next();
            }
        }
    while (*ptrA != *endA) {
        ++count;
        ptrA->next();
    }
    while (*ptrB != *endB) {
        ++count;
        ptrB->next();
    }

    const long long patternDispersion = patternSum2 * count - patternSum * patternSum;
    const long long candidateDispersion = candidateSum2 * count - candidateSum * candidateSum;
    const long long numerator = correlationSum * count - candidateSum * patternSum;

    if ((patternDispersion == 0) || (candidateDispersion == 0)) {
        return 0;
    }

    int orderDiff = ::log10(patternDispersion) - ::log10(candidateDispersion);
    int patternMult = 1000;
    if (orderDiff >= 5) {
        patternMult = 1000000;
    } else if (orderDiff >= 3) {
        patternMult = 100000;
    } else if (orderDiff >= 1) {
        patternMult = 10000;
    } else if (orderDiff <= -1) {
        patternMult = 100;
    } else if (orderDiff <= -3) {
        patternMult = 10;
    } else if (orderDiff <= -5) {
        patternMult = 1;
    }
    int candidateMult = 1000000 / patternMult;

    const long long quotient1 = (patternMult * numerator / patternDispersion);
    const long long quotient2 = (candidateMult * numerator / candidateDispersion);
    const int sign = (numerator >= 0) ? 1 : -1;

    return sign * quotient1 * quotient2;
}

int StatisticMatcher::criterion(const StatisticsTag &otherStatistics) const {
    return correlation(otherStatistics, *mStatisticsTag);
}
