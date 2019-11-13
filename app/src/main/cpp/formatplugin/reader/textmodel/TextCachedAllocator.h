// author : newbiechen
// date : 2019-10-06 15:26
// description : 文本缓存区域分配器
//

#ifndef NBREADER_TEXTCACHEMANAGER_H
#define NBREADER_TEXTCACHEMANAGER_H

#include <string>
#include <util/UnicodeUtil.h>

class TextCachedAllocator {
public:
    TextCachedAllocator(const size_t rowSize, const std::string &directoryName,
                        const std::string &fileExtension);

    ~TextCachedAllocator();

    char *allocate(size_t size);

    char *reallocateLast(char *ptr, size_t newSize);

    void flush();

    static char *writeUInt16(char *ptr, uint16_t value) {
        // 先写入前 8 位
        *ptr++ = value;
        // 再写入后 8 位
        *ptr++ = value >> 8;
        return ptr;
    }

    static char *writeUInt32(char *ptr, uint32_t value) {
        *ptr++ = value;
        value >>= 8;
        *ptr++ = value;
        value >>= 8;
        *ptr++ = value;
        value >>= 8;
        *ptr++ = value;
        return ptr;
    }

    static char *writeString(char *ptr, const UnicodeUtil::Ucs2String &str) {
        const size_t size = str.size();
        writeUInt16(ptr, size);
        memcpy(ptr + 2, &str.front(), size * 2);
        return ptr + size * 2 + 2;
    }

    static uint16_t readUInt16(const char *ptr) {
        const uint8_t *tmp = (const uint8_t *) ptr;
        return *tmp + ((uint16_t) *(tmp + 1) << 8);
    }

    static uint32_t readUInt32(const char *ptr) {
        const uint8_t *tmp = (const uint8_t *) ptr;
        return *tmp
               + ((uint32_t) *(tmp + 1) << 8)
               + ((uint32_t) *(tmp + 2) << 16)
               + ((uint32_t) *(tmp + 3) << 24);
    }

public:
    const std::string &directoryName() const {
        return mDirectoryName;
    }

    const std::string &fileExtension() const {
        return mFileExtension;
    }

    // 数据块数量
    size_t getBufferBlockCount() const {
        return mBufferBlockList.size();
    }

    size_t getCurBufferBlockOffset() const {
        return mCurBlockOffset;
    }

    bool isFailed() const {
        return hasFailed;
    }

private:
    std::string createFileName(size_t index);

    void writeCache(size_t blockLength);

private:
    // 默认创建缓冲块的大小
    const size_t mBasicBufferBlockSize;
    // 实际创建缓冲块的大小
    size_t mActualBufferBlockSize;
    // 存储创建的所有缓冲区指针
    std::vector<char *> mBufferBlockList;
    // 基于当前缓冲块的偏移
    size_t mCurBlockOffset;

    bool hasChanges;
    bool hasFailed;

    const std::string mDirectoryName;
    const std::string mFileExtension;

private: // disable copying
    TextCachedAllocator(const TextCachedAllocator &);

    const TextCachedAllocator &operator=(const TextCachedAllocator &);
};


#endif //NBREADER_TEXTCACHEMANAGER_H
