// author : newbiechen
// date : 2019-10-18 15:12
// description : 
//

#include "StatisticsTag.h"

StatisticsTag::StatisticsTag() : mCharSequenceSize(0), isVolumesUpToDate(true),
                                 mVolume(0), mSquaresVolume(0) {
}

StatisticsTag::StatisticsTag(std::size_t charSequenceSize) :
        mCharSequenceSize(charSequenceSize), isVolumesUpToDate(true),
        mVolume(0), mSquaresVolume(0) {
}

StatisticsTag::StatisticsTag(std::size_t charSequenceSize, std::size_t volume, unsigned long long squaresVolume) :
        mCharSequenceSize(charSequenceSize), isVolumesUpToDate(true),
        mVolume(volume), mSquaresVolume(squaresVolume) {
}

StatisticsTag::~StatisticsTag() {
}

std::size_t StatisticsTag::getVolume() const {
    if (!isVolumesUpToDate) {
        calculateVolumes();
    }
    return mVolume;
}

unsigned long long StatisticsTag::getSquaresVolume() const {
    if (!isVolumesUpToDate) {
        calculateVolumes();
    }
    return mSquaresVolume;
}

NativeStatisticsTag::NativeStatisticsTag() : StatisticsTag() {
}

// 保存了 buffer 中所有行起始字的指针
NativeStatisticsTag::NativeStatisticsTag(const ItemMap &dictionary) {
    if (!dictionary.empty()) {
        // 字对应的字节数
        mCharSequenceSize = dictionary.begin()->first.getSize();
        isVolumesUpToDate = false;
        mItemMap = dictionary;
    } else {
        mCharSequenceSize = 0;
        isVolumesUpToDate = true;
        mVolume = 0;
        mSquaresVolume = 0;
    }
}

NativeStatisticsTag::~NativeStatisticsTag() {
}

void NativeStatisticsTag::calculateVolumes() const {
    mVolume = 0;
    mSquaresVolume = 0;
    for (ItemMap::const_iterator it = mItemMap.begin(); it != mItemMap.end(); ++it) {
        const std::size_t frequency = it->second;
        mVolume += frequency;
        mSquaresVolume += frequency * frequency;
    }
    isVolumesUpToDate = true;
}

NativeStatisticsTag NativeStatisticsTag::top(std::size_t amount) const {
    if (mItemMap.empty()) {
        return NativeStatisticsTag();
    }
    if (amount >= mItemMap.size()) {
        return *this;
    }
    ItemMap dictionary;
    ItemVector tempItemVector;
    tempItemVector.resize(mItemMap.size());
    std::copy(mItemMap.begin(), mItemMap.end(), tempItemVector.begin());
    std::sort(tempItemVector.rbegin(), tempItemVector.rend(), LessFrequency());
    ItemVector::const_iterator it = tempItemVector.begin();
    while (amount != 0) {
        dictionary[it->first] = it->second;
        ++it;
        --amount;
    }
    return NativeStatisticsTag(dictionary);
}

void NativeStatisticsTag::retain(const NativeStatisticsTag &other) {
    if (this == &other) {
        return;
    }
    if (mCharSequenceSize == other.mCharSequenceSize) {
        mVolume = 0;
        mSquaresVolume = 0;

        ItemMap::iterator itA = mItemMap.begin();
        ItemMap::const_iterator itB = other.mItemMap.begin();
        const ItemMap::iterator endA = mItemMap.end();
        const ItemMap::const_iterator endB = other.mItemMap.end();

        while ((itA != endA) && (itB != endB)) {
            const int comparison = itA->first.compareTo(itB->first);
            if (comparison < 0) {
                mItemMap.erase(itA++);
            } else if (comparison > 0) {
                ++itB;
            } else {
                itA->second += itB->second;
                mVolume += itA->second;
                mSquaresVolume += itA->second * itA->second;
                ++itA;
                ++itB;
            }
        }
        if (itA != endA) {
            mItemMap.erase(itA, endA);
        }
        isVolumesUpToDate = true;
    } else {
        *this = NativeStatisticsTag();
    }
}

void NativeStatisticsTag::scaleToShort() {
    const std::size_t maxFrequency = std::max_element(mItemMap.begin(), mItemMap.end(), LessFrequency())->second;
    const std::size_t maxShort = 65535;
    if (maxFrequency > maxShort) {
        const std::size_t devider = maxFrequency / maxShort + 1;
        ItemMap::iterator it = mItemMap.begin();
        const ItemMap::iterator end = mItemMap.end();
        while (it != end) {
            if (it->second < devider) {
                mItemMap.erase(it++);
            } else {
                it->second /= devider;
                ++it;
            }
        }
    }
}

std::shared_ptr<StatisticsItemTag> NativeStatisticsTag::begin() const {
    return std::make_shared<NativeStatisticsItemTag>(mItemMap.begin(), 0);
}

std::shared_ptr<StatisticsItemTag> NativeStatisticsTag::end() const {
    return std::make_shared<NativeStatisticsItemTag>(mItemMap.end(), mItemMap.size());
}

XMLStatisticsTag::XMLStatisticsTag() : StatisticsTag(),
                                       mCapacity(0), mBack(0), mSequences(0), mFrequencies(0) {
}

XMLStatisticsTag::XMLStatisticsTag(std::size_t charSequenceSize, std::size_t size, std::size_t volume,
                                   unsigned long long squaresVolume) :
        StatisticsTag(charSequenceSize, volume, squaresVolume), mCapacity(size) {
    mBack = 0;
    mSequences = new char[mCharSequenceSize * size];
    mFrequencies = new unsigned short[size];
}

XMLStatisticsTag::~XMLStatisticsTag() {
    if (mSequences != 0) {
        delete[] mSequences;
        delete[] mFrequencies;
    }
}

void XMLStatisticsTag::insert(const CharSequence &charSequence, std::size_t frequency) {
    if (mBack == mCapacity) {
        return;
    }
    for (std::size_t i = 0; i < mCharSequenceSize; ++i) {
        mSequences[mBack * mCharSequenceSize + i] = charSequence[i];
    }
    mFrequencies[mBack] = (unsigned short) frequency;
    ++mBack;
    //isVolumesUpToDate = false;
}

void XMLStatisticsTag::calculateVolumes() const {
    mVolume = 0;
    mSquaresVolume = 0;
    for (std::size_t i = 0; i != mBack; ++i) {
        const std::size_t frequency = mFrequencies[i];
        mVolume += frequency;
        mSquaresVolume += frequency * frequency;
    }
    isVolumesUpToDate = true;
}

std::shared_ptr<StatisticsItemTag> XMLStatisticsTag::begin() const {
    return std::make_shared<XMLStatisticsItemTag>(mCharSequenceSize, mSequences, mFrequencies, 0);
}

std::shared_ptr<StatisticsItemTag> XMLStatisticsTag::end() const {
    return std::make_shared<XMLStatisticsItemTag>(mCharSequenceSize, mSequences + mBack * mCharSequenceSize,
                                                  mFrequencies + mBack, mBack);
}

XMLStatisticsTag &XMLStatisticsTag::operator=(const XMLStatisticsTag &other) {
    if (this == &other) {
        return *this;
    }
    mCharSequenceSize = other.mCharSequenceSize;
    isVolumesUpToDate = false;
    if (mSequences != 0) {
        delete[] mSequences;
        delete[] mFrequencies;
    }
    mCapacity = other.mCapacity;
    mBack = 0;
    if (other.mSequences != 0) {
        mSequences = new char[mCapacity * other.mCharSequenceSize];
        mFrequencies = new unsigned short[mCapacity];
        while (mBack < other.mBack) {
            mSequences[mBack] = other.mSequences[mBack];
            mFrequencies[mBack] = other.mFrequencies[mBack];
            ++mBack;
        }
    } else {
        mSequences = 0;
        mFrequencies = 0;
    }
    return *this;
}
