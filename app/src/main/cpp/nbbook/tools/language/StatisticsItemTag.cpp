// author : newbiechen
// date : 2019-10-18 15:14
// description : 
//

#include "StatisticsItemTag.h"

NativeStatisticsItemTag::NativeStatisticsItemTag(
        const std::map<CharSequence, std::size_t>::const_iterator it, std::size_t index)
        : StatisticsItemTag(index),
          mIterator(it) {
}

CharSequence NativeStatisticsItemTag::sequence() const {
    return mIterator->first;
}

std::size_t NativeStatisticsItemTag::frequency() const {
    return mIterator->second;
}

void NativeStatisticsItemTag::next() {
    ++mIndex;
    ++mIterator;
}

XMLStatisticsItemTag::XMLStatisticsItemTag(std::size_t sequenceLength,
                                           char *sequencePtr,
                                           unsigned short *frequencyPtr,
                                           std::size_t index) :
        StatisticsItemTag(index),
        mSequencePtr(sequencePtr),
        mFrequencyPtr(frequencyPtr),
        mSequenceLength(sequenceLength) {
}

CharSequence XMLStatisticsItemTag::sequence() const {
    return CharSequence(mSequencePtr, mSequenceLength);
}

std::size_t XMLStatisticsItemTag::frequency() const {
    return (std::size_t) *mFrequencyPtr;
}

void XMLStatisticsItemTag::next() {
    ++mIndex;
    mSequencePtr += mSequenceLength;
    ++mFrequencyPtr;
}

