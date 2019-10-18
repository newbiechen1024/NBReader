// author : newbiechen
// date : 2019-10-18 17:15
// description :
//

#include "StatisticsNativeReader.h"

StatisticsNativeReader::StatisticsNativeReader(const std::string &breakSymbols) {
    mBreakSymbolsTable = new char[256];
    memset(mBreakSymbolsTable, 0, 256);
    for (int i = breakSymbols.size() - 1; i >= 0; --i) {
        mBreakSymbolsTable[(unsigned char) breakSymbols[i]] = 1;
    }
}

StatisticsNativeReader::~StatisticsNativeReader() {
    delete[] mBreakSymbolsTable;
}

void StatisticsNativeReader::readNativeStatistics(const char *buffer, std::size_t length,
                                                  std::size_t charSequenceSize, NativeStatisticsTag &statistics) {
    const char *start = buffer;
    const char *end = buffer + length;
    std::map<CharSequence, std::size_t> dictionary;
    std::size_t locker = charSequenceSize;
    for (const char *ptr = start; ptr < end;) {
        // 判断用于判断的 buffer 是否具有 \n\r
        if (mBreakSymbolsTable[(unsigned char) *(ptr)] == 1) {
            locker = charSequenceSize;
        } else if (locker != 0) {
            --locker;
        }
        // 获取 \n\r 后的第一个字节位置
        if (locker == 0) {
            const char *sequenceStart = ptr - charSequenceSize + 1;
            // 生成 CharSequence 对象，存储 3 字节表示存储该字。
            // 并将该字存储到 map 中，并将当前字出现次数 +1
            ++dictionary[CharSequence(sequenceStart, charSequenceSize)];
        }
        ++ptr;
    }
    statistics = NativeStatisticsTag(dictionary);
}
