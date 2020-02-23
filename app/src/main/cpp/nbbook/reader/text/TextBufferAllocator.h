// author : newbiechen
// date : 2019-12-30 13:37
// description : 文本缓冲区分配器
//

#ifndef NBREADER_TEXTBUFFERALLOCATOR_H
#define NBREADER_TEXTBUFFERALLOCATOR_H

#include <string>
#include "../../util/UnicodeUtil.h"
#include "../../filesystem/buffer/CharBuffer.h"

class TextBufferAllocator {
public:
    static char *writeUInt16(char *ptr, uint16_t value) {
        *ptr++ = (0xff00 & value) >> 8;
        *ptr++ = 0xff & value;
        return ptr;
    }

    static char *writeUInt32(char *ptr, uint32_t value) {
        *ptr++ = (0xff000000 & value) >> 24;
        *ptr++ = (0xff0000 & value) >> 16;
        *ptr++ = (0xff00 & value) >> 8;
        *ptr++ = 0xff & value;
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
        return ((uint16_t) *(tmp) << 8) + ((uint16_t) *(tmp + 1));
    }

    static uint32_t readUInt32(const char *ptr) {
        const uint8_t *tmp = (const uint8_t *) ptr;
        return ((uint32_t) *tmp << 24)
               + ((uint32_t) *(tmp + 1) << 16)
               + ((uint32_t) *(tmp + 2) << 8)
               + ((uint32_t) *(tmp + 3));
    }

public:
    TextBufferAllocator(const size_t defaultBufferSize);

    ~TextBufferAllocator();

    char *allocate(size_t size);

    /**
     * 重新对刚才 @see allocate() 获取的 ptr 进行分配
     * @param ptr:最近一次使用 allocate() 返回的 ptr
     * @param newSize
     * @return
     */
    char *reallocateLast(char *ptr, size_t newSize);

    /**
     * 获取 buffer 指针
     * @param buffer
     * @return
     */
    size_t close(char **buffer);

public:
    // 获取缓冲区当前的偏移值
    size_t getBufferOffset();

private:
    // 默认缓冲区大小
    const size_t mDefaultBufferSize;

    // 缓冲块列表
    std::vector<CharBuffer *> mBufferBlockList;

    // 缓冲区的偏移
    size_t mBufferOffset;

    // 当前缓冲区的偏移
    size_t mCurBlockOffset;

    // 当前缓冲块的大小
    size_t mCurBlockSize;

private: // disable copying
    TextBufferAllocator(const TextBufferAllocator &);

    const TextBufferAllocator &operator=(const TextBufferAllocator &);
};


#endif //NBREADER_TEXTBUFFERALLOCATOR_H
