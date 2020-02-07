// author : newbiechen
// date : 2019-10-18 17:15
// description : 将本地文字转换成 Statistics 标签数据
//

#ifndef NBREADER_STATISTICSNATIVEREADER_H
#define NBREADER_STATISTICSNATIVEREADER_H

#include <string>
#include "StatisticsTag.h"

class StatisticsNativeReader {

public:
    StatisticsNativeReader(const std::string &breakSymbols);

    ~StatisticsNativeReader();

    void readNativeStatistics(const char *buffer, std::size_t length,
                              std::size_t charSequenceSize,
                              NativeStatisticsTag &statistics);

private:
    char *mBreakSymbolsTable;
};


#endif //NBREADER_STATISTICSNATIVEREADER_H
