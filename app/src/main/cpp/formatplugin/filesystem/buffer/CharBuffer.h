// author : newbiechen
// date : 2019-12-04 15:01
// description : 
//

#ifndef NBREADER_CHARBUFFER_H
#define NBREADER_CHARBUFFER_H

#include <string>

/**
 * TODO:是否需要 offset
 */
class CharBuffer {
public:
    /**
     * CharBuffer 自己创建缓冲区
     */
    CharBuffer(size_t bufferLen);

    /**
     * 允许传入缓冲区
     * @param buffer
     * @param bufferLen
     */
    CharBuffer(char *buffer, size_t bufferLen);

    ~CharBuffer();

    /**
     * 获取下一缓冲区的位置
     * @return
     */
    size_t position() const {
        return mPosition;
    }

    /**
     * 设置当前位置
     * @param newPosition
     * @return
     */
    size_t position(size_t newPosition) {
        if (newPosition >= 0 && newPosition <= mBufferLen) {
            mPosition = newPosition;
        } else {
            // TODO:抛出异常
        }

        return mPosition;
    }

    /**
     * 获取缓冲区的大小
     * @return
     */
    size_t size() const {
        return mBufferLen;
    }

    char *buffer() {
        return mBuffer;
    }

    const char *iterator() const {
        return mBuffer;
    }

    /**
     * 重置 position
     */
    void reset() {
        mPosition = 0;
    }

    /**
     * 初始化 buffer
     * 重置 position
     */
    void clear() {
        mPosition = 0;
        memset(mBuffer, 0, mBufferLen);
    }

    void put(char ch);

    /**
    * 重置 buffer
    * 将 position 到 bufferLen 的剩余数据，移动到 buffer 的头部
    */
    void compact();

private:
    // 指向缓冲区的下一字节位置
    size_t mPosition;
    // 缓冲区的大小
    size_t mBufferLen;
    // 缓冲区
    char *mBuffer;
    // 是否是内部创建的 buffer
    bool isInnerBuffer;
};


#endif //NBREADER_CHARBUFFER_H
