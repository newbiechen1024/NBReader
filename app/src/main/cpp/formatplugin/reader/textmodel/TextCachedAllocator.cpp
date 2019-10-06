// author : newbiechen
// date : 2019-10-06 15:26
// description :
// 1. 创建文本缓冲，并写入到本地硬盘上
// 2. 新的文本缓冲，会复制上一个文本缓冲的数据。
// 3. 每个文本缓冲都会创建一个对应的 file

#include <filesystem/File.h>
#include <util/StringUtil.h>
#include "TextCachedAllocator.h"

TextCachedAllocator::TextCachedAllocator(const size_t rowSize, const std::string &directoryName,
                                       const std::string &fileExtension) : mRowSize(rowSize), mCurrentRowSize(0),
                                                                           mOffset(0), hasChanges(false),
                                                                           hasFailed(false),
                                                                           mDirectoryName(directoryName),
                                                                           mFileExtension(fileExtension) {
    // 在硬盘中创建目录
    File(directoryName).mkdirs();
}

TextCachedAllocator::~TextCachedAllocator() {
    flush();
    // 删除缓冲区的文本
    for (std::vector<char *>::const_iterator it = mPool.begin(); it != mPool.end(); ++it) {
        delete[] *it;
    }
}

char *TextCachedAllocator::allocate(size_t size) {
    hasChanges = true;
    // 如果文本池没有数据
    if (mPool.empty()) {
        // 将当前行的最大长度创建文本对象
        mCurrentRowSize = std::max(mRowSize, size + 2 + sizeof(char *));
        mPool.push_back(new char[mCurrentRowSize]);
    } else if (mOffset + size + 2 + sizeof(char *) > mCurrentRowSize) { // 如果传入的 size 大于最大缓冲
        // 重新创建一份缓冲区
        mCurrentRowSize = std::max(mRowSize, size + 2 + sizeof(char *));
        char *row = new char[mCurrentRowSize];
        // 将旧缓冲区的数据，分配到新的缓冲去上
        char *ptr = mPool.back() + mOffset;
        *ptr++ = 0;
        *ptr++ = 0;
        
        std::memcpy(ptr, &row, sizeof(char *));
        // 将缓冲写入到文本
        writeCache(mOffset + 2);
        // 添加到列表中
        mPool.push_back(row);
        mOffset = 0;
    }
    char *ptr = mPool.back() + mOffset;
    mOffset += size;
    return ptr;
}


char *TextCachedAllocator::reallocateLast(char *ptr, size_t newSize) {
    hasChanges = true;
    const size_t oldOffset = ptr - mPool.back();
    if (oldOffset + newSize + 2 + sizeof(char *) <= mCurrentRowSize) {
        mOffset = oldOffset + newSize;
        return ptr;
    } else {
        mCurrentRowSize = std::max(mRowSize, newSize + 2 + sizeof(char *));
        char *row = new char[mCurrentRowSize];
        std::memcpy(row, ptr, mOffset - oldOffset);

        *ptr++ = 0;
        *ptr++ = 0;
        std::memcpy(ptr, &row, sizeof(char *));
        writeCache(oldOffset + 2);

        mPool.push_back(row);
        mOffset = newSize;
        return row;
    }
}

void TextCachedAllocator::flush() {
    if (!hasChanges) {
        return;
    }
    // 删除 offset 后多余的 char 文本，并写入到 file 
    char *ptr = mPool.back() + mOffset;
    *ptr++ = 0;
    *ptr = 0;
    writeCache(mOffset + 2);
    hasChanges = false;
}

std::string TextCachedAllocator::createFileName(size_t index) {
    std::string name(mDirectoryName);
    name.append("/");
    // 将 index 作为 file 的名字
    StringUtil::appendNumber(name, index);
    // 添加尾缀
    return name.append(".").append(mFileExtension);
}

void TextCachedAllocator::writeCache(size_t blockLength) {
    // 如果出错，或者池子中没有数据，则返回
    if (hasFailed || mPool.size() == 0) {
        return;
    }
    const size_t index = mPool.size() - 1;
    const std::string fileName = createFileName(index);
    File file(fileName);
    // 获取文本的输出流
    std::shared_ptr<OutputStream> stream = file.getOutputStream();
    if (stream == nullptr || !stream->open()) {
        hasFailed = true;
        return;
    }
    // 将缓冲区的数据写入到文本中
    stream->write(mPool[index], blockLength);
    stream->close();
}