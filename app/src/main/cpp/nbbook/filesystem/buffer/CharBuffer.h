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

    CharBuffer(char *buffer, size_t position, size_t length);

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
    void position(size_t newPosition) {
        if (newPosition > mLimit) {
            // TODO:抛出异常，暂时不处理
        }

        mPosition = newPosition;
    }

    size_t limit() const {
        return mLimit;
    }

    void limit(size_t newLimit) {
        mLimit = newLimit;
        if (mPosition > newLimit) mPosition = newLimit;
    }

    /**
     * 获取缓冲区的大小
     * @return
     */
    size_t size() const {
        return mBufferLen;
    }

    size_t remaining() const {
        return mLimit - mPosition;
    }

    char *buffer() {
        return mBuffer;
    }

    const char *iterator() const {
        return mBuffer;
    }

    /**
     * position 与 limit 进行翻转
     */
    void flip() {
        mLimit = mPosition;
        mPosition = 0;
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
        mLimit = mBufferLen;
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

    // buffer 限制读取区域
    size_t mLimit;

    // 缓冲区的大小
    size_t mBufferLen;
    // 缓冲区
    char *mBuffer;
    // 是否是内部创建的 buffer
    bool isInnerBuffer;
};


#endif //NBREADER_CHARBUFFER_H
