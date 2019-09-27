// author : newbiechen
// date : 2019-09-26 18:27
// description : zip 文件头信息
//

#ifndef NBREADER_ZIPHEADER_H
#define NBREADER_ZIPHEADER_H


#include "../io/InputStream.h"

struct ZipItemHeader {
    static const int SIGNATURE_LOCAL_FILE;
    static const int SIGNATURE_DATA;
    static const int SIGNATURE_CENTRAL_DIRECTORY;
    static const int SIGNATURE_END_OF_CENTRAL_DIRECTORY;

    unsigned long signature;
    unsigned short version;
    unsigned short flags;
    unsigned short compressionMethod;
    unsigned short modificationTime;
    unsigned short modificationDate;
    unsigned long CRC32;
    unsigned long compressedSize;
    unsigned long uncompressedSize;
    unsigned short nameLength;
    unsigned short extraLength;
};

// Zip Entry Item Header 探测器
class ZipHeaderDetector {
public:
    ~ZipHeaderDetector();

    // 读取 zip 包中 item 的 header 信息
    static bool readItemHeader(InputStream &stream, ZipItemHeader &header);

    // 跳过 zip 包中 item 的内容
    static void skipItemInfo(InputStream &stream, ZipItemHeader &header);

private:
    static unsigned short readShort(InputStream &stream);

    static unsigned long readLong(InputStream &stream);

    ZipHeaderDetector();
};

#endif //NBREADER_ZIPHEADER_H
