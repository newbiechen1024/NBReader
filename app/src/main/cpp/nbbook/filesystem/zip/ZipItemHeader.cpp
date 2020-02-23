// author : newbiechen
// date : 2019-09-26 18:27
// description : 
//

#include "ZipItemHeader.h"
#include "ZipDecompressor.h"
#include "../../util/Logger.h"

const int ZipItemHeader::SIGNATURE_CENTRAL_DIRECTORY = 0x02014B50;
const int ZipItemHeader::SIGNATURE_LOCAL_FILE = 0x04034B50;
const int ZipItemHeader::SIGNATURE_END_OF_CENTRAL_DIRECTORY = 0x06054B50;
const int ZipItemHeader::SIGNATURE_DATA = 0x08074B50;

bool ZipHeaderDetector::readItemHeader(InputStream &stream, ZipItemHeader &header) {
    size_t startOffset = stream.offset();
    header.signature = readLong(stream);
    switch (header.signature) {
        default:
            return stream.offset() == startOffset + 4;
        case ZipItemHeader::SIGNATURE_CENTRAL_DIRECTORY: { // 如果是目录
            header.version = readLong(stream);
            header.flags = readShort(stream);
            header.compressionMethod = readShort(stream);
            header.modificationTime = readShort(stream);
            header.modificationDate = readShort(stream);
            header.CRC32 = readLong(stream);
            header.compressedSize = readLong(stream);
            header.uncompressedSize = readLong(stream);
            if (header.compressionMethod == 0 && header.compressedSize != header.uncompressedSize) {
                header.compressedSize = header.uncompressedSize;
            }
            header.nameLength = readShort(stream);
            header.extraLength = readShort(stream);
            const unsigned short toSkip = readShort(stream);
            stream.seek(12 + header.nameLength + header.extraLength + toSkip, false);
            return stream.offset() ==
                   startOffset + 42 + header.nameLength + header.extraLength + toSkip;
        }
        case ZipItemHeader::SIGNATURE_LOCAL_FILE: // 如果是文件

            header.version = readShort(stream);
            header.flags = readShort(stream);
            header.compressionMethod = readShort(stream);
            header.modificationTime = readShort(stream);
            header.modificationDate = readShort(stream);
            header.CRC32 = readLong(stream);
            header.compressedSize = readLong(stream);
            header.uncompressedSize = readLong(stream);
            if (header.compressionMethod == 0 && header.compressedSize != header.uncompressedSize) {
                header.compressedSize = header.uncompressedSize;
            }
            header.nameLength = readShort(stream);
            header.extraLength = readShort(stream);
            return stream.offset() == startOffset + 30 && header.nameLength != 0;
        case ZipItemHeader::SIGNATURE_END_OF_CENTRAL_DIRECTORY: {

            stream.seek(16, false);
            const unsigned short toSkip = readShort(stream);
            stream.seek(toSkip, false);
            header.uncompressedSize = 0;
            return stream.offset() == startOffset + 18 + toSkip;
        }
        case ZipItemHeader::SIGNATURE_DATA:

            header.CRC32 = readLong(stream);
            header.compressedSize = readLong(stream);
            header.uncompressedSize = readLong(stream);
            header.nameLength = 0;
            header.extraLength = 0;
            return stream.offset() == startOffset + 16;
    }
}

unsigned short ZipHeaderDetector::readShort(InputStream &stream) {
    char buffer[2];
    stream.read(buffer, 2);
    return ((((unsigned short) buffer[1]) & 0xFF) << 8) + ((unsigned short) buffer[0] & 0xFF);
}

unsigned long ZipHeaderDetector::readLong(InputStream &stream) {
    char buffer[4];
    stream.read(buffer, 4);

    return
            ((((unsigned long) buffer[3]) & 0xFF) << 24) +
            ((((unsigned long) buffer[2]) & 0xFF) << 16) +
            ((((unsigned long) buffer[1]) & 0xFF) << 8) +
            ((unsigned long) buffer[0] & 0xFF);
}

void ZipHeaderDetector::skipItemInfo(InputStream &stream, ZipItemHeader &header) {
    switch (header.signature) {
        case ZipItemHeader::SIGNATURE_LOCAL_FILE:
            if ((header.flags & 0x08) == 0x08 && header.compressionMethod != 0) {
                stream.seek(header.extraLength, false);
                ZipDecompressor decompressor((size_t) -1);
                size_t size;
                do {
                    size = decompressor.decompress(stream, 0, 2048);
                    header.uncompressedSize += size;
                } while (size == 2048);
            } else {
                stream.seek(header.extraLength + header.compressedSize, false);
            }
            break;
        default:
            break;
    }
}