// author : newbiechen
// date : 2019-10-18 15:12
// description : xml 中的 Statistics 标签
// 标签示例：
// <statistics charSequenceSize="3" size="300" volume="1017864" squaresVolume="10820972808">
//   <item sequence="0x2e 0x2e 0x2e" frequency="960"/>
// </statistics>

#ifndef NBREADER_STATISTICSTAG_H
#define NBREADER_STATISTICSTAG_H

#include <string>
#include <memory>
#include <map>
#include "StatisticsItemTag.h"
#include "CharSequence.h"
#include <vector>

class StatisticsTag {
public:
    StatisticsTag();

    StatisticsTag(size_t charSequenceSize);

    StatisticsTag(size_t charSequenceSize, size_t volume, unsigned long long squaresVolume);

    virtual ~StatisticsTag();

    size_t getVolume() const;

    unsigned long long getSquaresVolume() const;

    size_t getCharSequenceSize() const {
        return mCharSequenceSize;
    }

public:
    // 获取第一个 subItem
    virtual std::shared_ptr<StatisticsItemTag> begin() const = 0;

    // 获取最后一个 subItem
    virtual std::shared_ptr<StatisticsItemTag> end() const = 0;

protected:
    virtual void calculateVolumes() const = 0;

protected:
    size_t mCharSequenceSize;
    mutable bool isVolumesUpToDate;
    mutable size_t mVolume;
    mutable unsigned long long mSquaresVolume;
};

/**
 * 解析 buffer 获取到的 subItem 数据
 */
class NativeStatisticsTag : public StatisticsTag {
private:
    // 存储 item 的 array
    typedef std::vector<std::pair<CharSequence, std::size_t>> ItemVector;
    //
    typedef std::map<CharSequence, std::size_t> ItemMap;

public:
    NativeStatisticsTag();

    NativeStatisticsTag(const ItemMap &statItemMap);

    ~NativeStatisticsTag();

    std::size_t getSize() const;

    NativeStatisticsTag top(std::size_t amount) const;

    void scaleToShort();

    void retain(const NativeStatisticsTag &other);

    bool empty() const {
        return mItemMap.empty();
    }

    virtual std::shared_ptr<StatisticsItemTag> begin() const;

    virtual std::shared_ptr<StatisticsItemTag> end() const;

protected:
    void calculateVolumes() const;

private:
    struct LessFrequency {
        bool operator()(const std::pair<CharSequence, std::size_t> a, const std::pair<CharSequence, std::size_t> b) {
            return (a.second < b.second);
        }
    };

private:
    ItemMap mItemMap;
};

/**
 * 从 xml 文件中得到的 subItem 数据
 */
class XMLStatisticsTag : public StatisticsTag {
public:
    XMLStatisticsTag();

    XMLStatisticsTag(std::size_t charSequenceSize, std::size_t size, std::size_t volume,
                     unsigned long long squaresVolume);

    ~XMLStatisticsTag();

    XMLStatisticsTag &operator=(const XMLStatisticsTag &other);

    void insert(const CharSequence &charSequence, std::size_t frequency);

    bool empty() const {
        return (mBack == 0);
    }

    virtual std::shared_ptr<StatisticsItemTag> begin() const;

    virtual std::shared_ptr<StatisticsItemTag> end() const;

protected:
    void calculateVolumes() const;

private:
    std::size_t mCapacity;
    std::size_t mBack;
    char *mSequences;
    unsigned short *mFrequencies;
};


#endif //NBREADER_STATISTICSTAG_H
