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
                                       const std::string &fileExtension) : mBasicBufferBlockSize(rowSize), mActualBufferBlockSize(0),
                                                                           mCurBlockOffset(0), hasChanges(false),
                                                                           hasFailed(false),
                                                                           mDirectoryName(directoryName),
                                                                           mFileExtension(fileExtension) {
    // 在硬盘中创建目录
    File(directoryName).mkdirs();
}

TextCachedAllocator::~TextCachedAllocator() {
    flush();
    // 删除缓冲区的文本
    for (std::vector<char *>::const_iterator it = mBufferBlockList.begin(); it != mBufferBlockList.end(); ++it) {
        delete[] *it;
    }
}

// TODO：1. 这部分代码可优化，将 char * 改用创建一个 BufferBlock 表示，要不然一般人看不懂。
// TODO：2. 没看懂作者为什么要持有下一个 bufferBlock 的指针，又无法写入到文本中。
// TODO：3. 根据上层代码(CachedCharStorage)显示，size + 2 这个 2 只是用来给 offset 判断是否达到数据尾部的提示，可加可不加。
char *TextCachedAllocator::allocate(size_t size) {
    hasChanges = true;
    // 如果文本池没有数据
    if (mBufferBlockList.empty()) {
        // 根据传入的大小，决定实际当前缓冲区的大小
        mActualBufferBlockSize = std::max(mBasicBufferBlockSize, size + 2 + sizeof(char *));
        // TODO:char 数组的数据结构 | size | 2 | char *|
        // TODO:size 表示数据    2 表示 size 和 char * 之间的间隔标记位   char * 表示指向的下一段缓冲区
        mBufferBlockList.push_back(new char[mActualBufferBlockSize]);
    } else if (mCurBlockOffset + size + 2 + sizeof(char *) > mActualBufferBlockSize) { // 如果
        // 确定实际创建缓冲块的大小
        mActualBufferBlockSize = std::max(mBasicBufferBlockSize, size + 2 + sizeof(char *));

        char *bufferBlock = new char[mActualBufferBlockSize];

        char *ptr = mBufferBlockList.back() + mCurBlockOffset;
        // 初始化间隔标记位
        *(ptr++) = 0;
        *(ptr++) = 0;

        // 将当前 bufferBlock 第一个字节的地址复制到，上一个 bufferBlock 的最后一个字节位置上
        std::memcpy(ptr, &bufferBlock, sizeof(char *));

        // 将缓冲区的数据 + 间隔标记位接入到文本中
        // TODO:并没有将下一个 bufferBlock 的地址写入到文件中，说明间隔标位，同时承担缓冲块的标记作用。
        writeCache(mCurBlockOffset + 2);

        // 添加到列表中
        mBufferBlockList.push_back(bufferBlock);
        // 重置当前缓冲块的偏移
        mCurBlockOffset = 0;
    }
    char *ptr = mBufferBlockList.back() + mCurBlockOffset;
    mCurBlockOffset += size;
    return ptr;
}


char *TextCachedAllocator::reallocateLast(char *ptr, size_t newSize) {
    // TODO: 传入的 ptr 必须是之前 push_back() 进去的 bufferBlock 的一部分才行 ==> 代码写的不严谨
    // 作用：如果之前请求分配的区域不够的话，需要重新申请分配内存的意思。 ==> 传入的 ptr 必须是最新 allocate 返回的值。
    hasChanges = true;
    const size_t oldOffset = ptr - mBufferBlockList.back();
    if (oldOffset + newSize + 2 + sizeof(char *) <= mActualBufferBlockSize) {
        mCurBlockOffset = oldOffset + newSize;
        return ptr;
    } else {
        mActualBufferBlockSize = std::max(mBasicBufferBlockSize, newSize + 2 + sizeof(char *));
        char *row = new char[mActualBufferBlockSize];
        std::memcpy(row, ptr, mCurBlockOffset - oldOffset);

        *ptr++ = 0;
        *ptr++ = 0;
        std::memcpy(ptr, &row, sizeof(char *));
        writeCache(oldOffset + 2);

        mBufferBlockList.push_back(row);
        mCurBlockOffset = newSize;
        return row;
    }
}

void TextCachedAllocator::flush() {
    if (!hasChanges) {
        return;
    }
    // 删除 offset 后多余的 char 文本，并写入到 file 
    char *ptr = mBufferBlockList.back() + mCurBlockOffset;
    *ptr++ = 0;
    *ptr = 0;
    writeCache(mCurBlockOffset + 2);
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

/**
 *
 * @param blockLength:从缓冲块中输出数据的长度
 * 将缓冲块数据输出到文本中，每一个缓冲块就创建一个文本文件。
 */
void TextCachedAllocator::writeCache(size_t blockLength) {
    // 如果出错，或者池子中没有数据，则返回
    if (hasFailed || mBufferBlockList.size() == 0) {
        return;
    }

    const size_t index = mBufferBlockList.size() - 1;
    const std::string fileName = createFileName(index);
    File file(fileName);
    // 获取文本的输出流
    std::shared_ptr<OutputStream> stream = file.getOutputStream();
    if (stream == nullptr || !stream->open()) {
        hasFailed = true;
        return;
    }
    // 将缓冲区的数据写入到文本中
    stream->write(mBufferBlockList[index], blockLength);
    stream->close();
}