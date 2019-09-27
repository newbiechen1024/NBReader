// author : newbiechen
// date : 2019-09-27 15:12
// description : 读取 zip 压缩包内容的输入流
//

#ifndef NBREADER_ZIPINPUTSTREAM_H
#define NBREADER_ZIPINPUTSTREAM_H


#include <memory>
#include <filesystem/File.h>
#include "../io/InputStream.h"
#include "ZipDecompressor.h"

class ZipInputStream : public InputStream {

public:

    ~ZipInputStream();

    // 打开输入流
    bool open();

    // 读取数据
    size_t read(char *buffer, size_t maxSize);

    // 跳到具体位置
    void seek(int offset, bool absoluteOffset);

    // 当前位置
    size_t offset() const;

    // 关闭输入流
    void close();

private:
    std::shared_ptr<InputStream> mInputStream;
    std::shared_ptr<ZipDecompressor> mDecompressor;
    std::string mZipPath;
    std::string mItemName;

    size_t mUncompressedSize;
    size_t mAvailableSize;
    size_t mOffset;

    bool isOpen;
    bool isDeflated;

    ZipInputStream(std::shared_ptr<InputStream> inputStream,
                   const std::string &zipPath, const std::string &itemName);

    friend File;
};


#endif //NBREADER_ZIPINPUTSTREAM_H
