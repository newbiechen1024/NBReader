// author : newbiechen
// date : 2019-12-04 15:01
// description : 
//

#include "CharBuffer.h"

CharBuffer::CharBuffer(size_t bufferLen) {
    mBuffer = new char[bufferLen];
    mBufferLen = bufferLen;
    mLimit = mBufferLen;
    mPosition = 0;

    isInnerBuffer = true;
}

CharBuffer::CharBuffer(char *buffer, size_t bufferLen) {
    mBuffer = buffer;
    mBufferLen = bufferLen;
    mLimit = mBufferLen;
    mPosition = 0;

    isInnerBuffer = false;
}

CharBuffer::CharBuffer(char *buffer, size_t position, size_t bufferLen) {
    mBuffer = buffer;
    mBufferLen = bufferLen;
    mLimit = mBufferLen;
    mPosition = position;

    isInnerBuffer = false;
}

CharBuffer::~CharBuffer() {
    // 如果是内部创建的 buffer 则自己创建
    if (isInnerBuffer) {
        delete[] mBuffer;
    }
}

void CharBuffer::put(char ch) {
    // 如果当前位置已经超出最大长度
    if (mPosition >= mLimit) {
        // TODO:要不要做异常处理？
        return;
    }

    char *curBuffer = mBuffer + mPosition;
    *curBuffer = ch;
    ++mPosition;
}

void CharBuffer::compact() {
    // 获取剩余缓冲区的数据
    size_t remainSize = remaining();
    // 说明数据已经全部读取完，直接走 clear
    if (remainSize == 0) {
        clear();
    } else {
        char remainBuffer[remainSize];
        char *bufferPtr = mBuffer + mPosition;

        // 将先复制到临时区域
        memcpy(remainBuffer, bufferPtr, remainSize);
        // 重置缓冲区
        clear();
        // 将数据写入到缓冲区头部
        memcpy(mBuffer, remainBuffer, remainSize);
        // 设置 position
        mPosition = remainSize;
    }
}