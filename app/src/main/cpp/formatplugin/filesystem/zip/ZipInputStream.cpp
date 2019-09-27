// author : newbiechen
// date : 2019-09-27 15:12
// description : 
//

#include "ZipInputStream.h"
#include "ZipEntry.h"
#include "ZipEntryManager.h"

// TODO :ZLInputStreamDecorator 的作用是什么。。暂不处理
ZipInputStream::ZipInputStream(std::shared_ptr<InputStream> inputStream, const std::string &zipPath,
                               const std::string &itemName) : mInputStream(inputStream), mZipPath(zipPath),
                                                              mItemName(itemName), isDeflated(false),
                                                              isOpen(false), mUncompressedSize(0) {

}

ZipInputStream::~ZipInputStream() {
    close();
}

bool ZipInputStream::open() {
    close();
    // 从管理器中查找对应的ZipItem
    ZipItemInfo itemInfo = ZipEntryManager::getInstance()->getZipEntry(mZipPath)
            ->getItemInfo(mItemName);
    if (!mInputStream->open()) {
        return false;
    }
    // 如果获取 info 失败
    if (itemInfo.offset == -1) {
        close();
        return false;
    }
    if (itemInfo.compressionMethod == 0) {
        isDeflated = false;
    } else if (itemInfo.compressionMethod == 8) {
        isDeflated = true;
    } else {
        close();
        return false;
    }

    mUncompressedSize = itemInfo.uncompressedSize;
    mAvailableSize = itemInfo.compressedSize;
    if (mAvailableSize == 0) {
        mAvailableSize = (size_t) -1;
    }

    if (isDeflated) {
        mDecompressor = std::make_shared(mAvailableSize);
    }

    mOffset = 0;
    isOpen = true;
    return true;
}

size_t ZipInputStream::read(char *buffer, size_t maxSize) {
    if (!isOpen) {
        return 0;
    }

    std::size_t realSize = 0;
    if (isDeflated) {
        realSize = mDecompressor->decompress(*mInputStream, buffer, maxSize);
        mOffset += realSize;
    } else {
        realSize = mInputStream->read(buffer, std::min(maxSize, mAvailableSize));
        mAvailableSize -= realSize;
        mOffset += realSize;
    }
    return realSize;
}

void ZipInputStream::close() {
    mDecompressor = nullptr;
    if (!mInputStream) {
        mInputStream->close();
    }
    isOpen = false;
}

void ZipInputStream::seek(int offset, bool absoluteOffset) {
    if (absoluteOffset) {
        offset -= this->offset();
    }
    if (offset > 0) {
        read(0, offset);
    } else if (offset < 0) {
        offset += this->offset();
        if (open() && offset > 0) {
            read(0, offset);
        }
    }
}

size_t ZipInputStream::offset() const {
    return mOffset;
}