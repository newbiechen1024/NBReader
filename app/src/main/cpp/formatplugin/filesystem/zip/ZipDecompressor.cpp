// author : newbiechen
// date : 2019-09-26 20:06
// description : 
//

#include "ZipDecompressor.h"

const std::size_t IN_BUFFER_SIZE = 2048;
const std::size_t OUT_BUFFER_SIZE = 32768;

ZipDecompressor::ZipDecompressor(size_t availableSize) : mAvailableSize(availableSize) {
    mZStream = new z_stream;
    // 初始化 stream
    memset(mZStream, 0, sizeof(z_stream));
    // 初始化解压器
    inflateInit2(mZStream, -MAX_WBITS);
    // 初始化缓冲区
    mInBuffer = new char[IN_BUFFER_SIZE];
    mOutBuffer = new char[OUT_BUFFER_SIZE];
}

ZipDecompressor::~ZLZDecompressor() {
    delete[](mInBuffer);
    delete[](mOutBuffer);
    inflateEnd(mZStream);
    delete (mZStream);
}

size_t ZipDecompressor::decompress(InputStream &stream, char *buffer, size_t maxSize) {
    // 如果 buffer 缓冲中的数据已经达到最大值
    while (mBuffer.length() < maxSize && mAvailableSize > 0) {
        // 设置读取数据的大小
        std::size_t size = std::min(mAvailableSize, (std::size_t) IN_BUFFER_SIZE);
        // 待解析的数据
        mZStream->next_in = (Bytef *) mInBuffer;
        // 待解析的数据大小
        mZStream->avail_in = stream.read(mInBuffer, size);
        if (mZStream->avail_in == size) {
            mAvailableSize -= size;
        } else {
            mAvailableSize = 0;
        }
        // 如果待解析的数据为 0，则退出
        if (mZStream->avail_in == 0) {
            break;
        }
        // 如果不为 0
        while (mZStream->avail_in > 0) {
            // 可输出的数据大小
            mZStream->avail_out = OUT_BUFFER_SIZE;
            // 输出数据缓冲
            mZStream->next_out = (Bytef *) mOutBuffer;

            int code = ::inflate(mZStream, Z_SYNC_FLUSH);
            // 判断是否解析出错
            if (code != Z_OK && code != Z_STREAM_END) {
                break;
            }
            //
            if (OUT_BUFFER_SIZE != mZStream->avail_out) {
                mBuffer.append(mOutBuffer, OUT_BUFFER_SIZE - mZStream->avail_out);
            }
            // 如果解析到末尾
            if (code == Z_STREAM_END) {
                mAvailableSize = 0;
                stream.seek(0 - mZStream->avail_in, false);
                break;
            }
        }
    }
    // 将 mBuffer 中的数据复制到 buffer 中
    std::size_t realSize = std::min(maxSize, mBuffer.length());
    if (buffer != 0) {
        std::memcpy(buffer, mBuffer.data(), realSize);
    }
    // 清空 mBuffer 中的数据
    mBuffer.erase(0, realSize);
    return realSize;
}