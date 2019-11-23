// author : newbiechen
// date : 2019-10-06 15:26
// description :
// 1. 创建文本缓冲，并写入到本地硬盘上
// 2. 新的文本缓冲，会复制上一个文本缓冲的数据。
// 3. 每个文本缓冲都会创建一个对应的 file


#include <filesystem/File.h>
#include <util/StringUtil.h>
#include <util/Logger.h>
#include "TextCachedAllocator.h"

static std::string getFilePath(const std::string &dir, const std::string &fileName,
                               const std::string &fileExtension) {
    std::string name(dir);
    name.append(FileSystem::separator);
    name.append(fileName);
    // 添加尾缀
    return name.append(".").append(fileExtension);
}

TextCachedAllocator::TextCachedAllocator(const size_t defaultBufferSize,
                                         const std::string &directoryName,
                                         const std::string &fileName,
                                         const std::string &fileExtension) : mBasicBufferBlockSize(defaultBufferSize),
                                                                             mBufferBlock(nullptr),
                                                                             mCacheFile(getFilePath(
                                                                                     directoryName,
                                                                                     fileName,
                                                                                     fileExtension)) {
    mActualBufferBlockSize = 0;
    mCurBlockOffset = 0;
    mLastTotalOffset = 0;
    hasChanges = false;
    hasFailed = false;

    // 在硬盘中创建目录
    File(directoryName).mkdirs();
}

TextCachedAllocator::~TextCachedAllocator() {
    flush();

    // 删除缓冲块的缓冲数据
    if (mBufferBlock != nullptr) {
        delete[] mBufferBlock;
    }
}

char *TextCachedAllocator::allocate(size_t size) {
    hasChanges = true;

    // 如果文本池没有数据
    if (mBufferBlock == nullptr) {
        // 根据传入的数据大小，决定实际当前缓冲区的大小
        mActualBufferBlockSize = std::max(mBasicBufferBlockSize, size);
        // 创建缓冲区
        mBufferBlock = new char[mActualBufferBlockSize];
    } else if (mCurBlockOffset + size > mActualBufferBlockSize) { // 如果当前数据偏移大于当前已分配缓冲区的大小

        // 如果新增的尺寸，超出了缓冲区大小，则将数据写入到本地
        writeCache(mCurBlockOffset);

        size_t newBufferBlockSize = std::max(mBasicBufferBlockSize, size);
        // 如果新缓冲块大小，大于上一缓冲块大小
        if (newBufferBlockSize > mActualBufferBlockSize) {
            mActualBufferBlockSize = newBufferBlockSize;
            delete[] mBufferBlock;
            mBufferBlock = new char[mActualBufferBlockSize];
        }

        // 重置缓冲区数据
        std::memset(mBufferBlock, 0, sizeof(char) * mActualBufferBlockSize);

        // 重置当前缓冲块的偏移
        mCurBlockOffset = 0;
    }
    char *ptr = mBufferBlock + mCurBlockOffset;
    mCurBlockOffset += size;
    return ptr;
}

char *TextCachedAllocator::reallocateLast(char *ptr, size_t newSize) {
    // 作用：如果之前请求分配的区域不够的话，需要重新申请分配内存的意思。 ==> 传入的 ptr 必须是最新 allocate 返回的值。
    hasChanges = true;
    const size_t oldOffset = ptr - mBufferBlock;
    if (oldOffset + newSize <= mActualBufferBlockSize) {
        mCurBlockOffset = oldOffset + newSize;
        return ptr;
    } else {
        // 如果新增的尺寸，超出了缓冲区大小，则将数据写入到本地
        writeCache(mCurBlockOffset);

        size_t newBufferBlockSize = std::max(mBasicBufferBlockSize, newSize);
        // 如果新缓冲块大小，大于上一缓冲块大小
        if (newBufferBlockSize > mActualBufferBlockSize) {
            mActualBufferBlockSize = newBufferBlockSize;
            delete[] mBufferBlock;
            mBufferBlock = new char[mActualBufferBlockSize];
        }

        // 重置缓冲区数据
        std::memset(mBufferBlock, 0, sizeof(char) * mActualBufferBlockSize);

        mCurBlockOffset = newSize;
        return mBufferBlock;
    }
}

void TextCachedAllocator::flush() {
    if (!hasChanges) {
        return;
    }

    writeCache(mCurBlockOffset);
    hasChanges = false;
}

/**
 * @param blockLength:从缓冲块中输出数据的长度
 * 将缓冲块输出到文本中。
 */
void TextCachedAllocator::writeCache(size_t blockLength) {
    // 如果出错，或者池子中没有数据，则返回
    if (hasFailed || mBufferBlock == nullptr) {
        return;
    }

    // 如果是第一次写入数据，并且旧文件存在，则直接删除
    if (mLastTotalOffset == 0 && mCacheFile.exists()) {
        mCacheFile.deleteFile();
    }

    // 获取文本的输出流
    std::shared_ptr<FileOutputStream> stream = mCacheFile.getOutputStream();

    if (stream == nullptr || !stream->open(true)) {
        hasFailed = true;
        return;
    }

    // 将缓冲区的数据写入到文本中
    stream->write(mBufferBlock, blockLength);
    // 关闭流
    stream->close();

    // 写入到本地的数据长度
    mLastTotalOffset += blockLength;
}