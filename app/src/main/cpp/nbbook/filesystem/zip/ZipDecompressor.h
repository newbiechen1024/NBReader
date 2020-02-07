// author : newbiechen
// date : 2019-09-26 20:06
// description : zip 解析器
//

#ifndef NBREADER_ZIPDECOMPRESSOR_H
#define NBREADER_ZIPDECOMPRESSOR_H

#include <stddef.h>
#include "../io/InputStream.h"
#include <zlib.h>
#include <string>

class ZipDecompressor {
public:
    ZipDecompressor(size_t availableSize);

    ~ZipDecompressor();

    size_t decompress(InputStream &stream, char *buffer, size_t maxSize);

private:
    // zip 流
    z_stream *mZStream;
    // zip 可解析的数据大小
    size_t mAvailableSize;
    char *mInBuffer;
    char *mOutBuffer;
    std::string mBuffer; // 作者用 string 代替 char * 作为缓冲区
};


#endif //NBREADER_ZIPDECOMPRESSOR_H
