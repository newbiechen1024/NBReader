// author : newbiechen
// date : 2019-10-18 15:14
// description : statistics 下的子标签 item
// 标签示例：
// <statistics charSequenceSize="3" size="300" volume="1017864" squaresVolume="10820972808">
//   <item sequence="0x2e 0x2e 0x2e" frequency="960"/>
// </statistics>

#ifndef NBREADER_STATISTICSITEMTAG_H
#define NBREADER_STATISTICSITEMTAG_H


#include "CharSequence.h"
#include <string>
#include <map>

class StatisticsItemTag {
public:
    StatisticsItemTag(std::size_t index) : mIndex(index) {
    }

    virtual ~StatisticsItemTag() {
    }

    virtual CharSequence sequence() const = 0;

    virtual std::size_t frequency() const = 0;

    virtual void next() = 0;

    bool operator==(const StatisticsItemTag &otherItem) const {
        return (this->index() == otherItem.index());
    }

    bool operator!=(const StatisticsItemTag &otherItem) const {
        return (this->index() != otherItem.index());
    }

    std::size_t index() const {
        return mIndex;
    }

protected:
    std::size_t mIndex;
};

/**
 * Native Statistics 标签的 item
 */
class NativeStatisticsItemTag : public StatisticsItemTag {
public:
    NativeStatisticsItemTag(const std::map<CharSequence, size_t>::const_iterator it, std::size_t index);

    CharSequence sequence() const override;

    std::size_t frequency() const override;

    void next() override;

private:
    std::map<CharSequence, size_t>::const_iterator mIterator;
};

/**
 * xml Statistics 标签的 item
 */
class XMLStatisticsItemTag : public StatisticsItemTag {
public:
    XMLStatisticsItemTag(std::size_t sequenceLength, char *sequencePtr,
                         unsigned short *frequencyPtr, std::size_t index);

    CharSequence sequence() const override;

    std::size_t frequency() const override;

    void next() override;

private:
    char const *mSequencePtr;
    unsigned short const *mFrequencyPtr;
    const std::size_t mSequenceLength;
};

#endif //NBREADER_STATISTICSITEMTAG_H
