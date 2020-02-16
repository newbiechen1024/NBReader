// author : newbiechen
// date : 2019-09-24 13:29
// description : 
//

#include "FileInputStream.h"

FileInputStream::FileInputStream(const File &file) : mFilePath(file.getPath()) {
    mFilePtr = nullptr;
}

FileInputStream::FileInputStream(const std::string &filePath) : mFilePath(filePath) {
    mFilePtr = nullptr;
}

FileInputStream::~FileInputStream() {
    close();
}

bool FileInputStream::open() {
    if (mFilePtr == nullptr) {
        // 使用二进制打开文件
        mFilePtr = fopen(mFilePath.c_str(), "rb");
    } else {
        // 如果再次打开 file，则指针跳转到头部
        fseek(mFilePtr, 0, SEEK_SET);
    }
    return mFilePtr != nullptr;
}

size_t FileInputStream::read(char *buffer, size_t maxSize) {
    if (mFilePtr == nullptr) {
        return 0;
    }

    // 缓冲区指针不为 nullptr
    if (buffer != nullptr) {
        // 按照 byte 正常读取
        return fread(buffer, 1, maxSize, mFilePtr);
    } else {
        // 如果缓冲区 buffer 为空，则 seek 到 cur + maxSize 的位置
        int pos = ftell(mFilePtr);
        fseek(mFilePtr, maxSize, SEEK_CUR);
        return ftell(mFilePtr) - pos;
    }
}

void FileInputStream::close() {
    if (mFilePtr != nullptr) {
        fclose(mFilePtr);
        mFilePtr = nullptr;
    }
}

void FileInputStream::seek(int offset, bool absoluteOffset) {
    if (mFilePtr == nullptr) {
        return;

    }

    fseek(mFilePtr, offset, absoluteOffset ? SEEK_SET : SEEK_CUR);
}

size_t FileInputStream::offset() const {
    return mFilePtr == nullptr ? 0 : ftell(mFilePtr);
}

size_t FileInputStream::length() const {
    if (mFilePtr == nullptr) {
        return 0;
    }

    long pos = ftell(mFilePtr);
    fseek(mFilePtr, 0, SEEK_END);
    long size = ftell(mFilePtr);
    fseek(mFilePtr, pos, SEEK_SET);

    return size;
}
