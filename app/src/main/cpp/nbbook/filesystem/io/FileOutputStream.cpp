// author : newbiechen
// date : 2019-09-27 17:08
// description :
//

#include <sys/stat.h>
#include "../File.h"
#include "FileOutputStream.h"

// TODO:输入输出流，应该分为 Unix 和 XXX ==> 这是专门针对 unix
FileOutputStream::FileOutputStream(const std::string &filePath) : mPath(filePath) {

}

FileOutputStream::FileOutputStream(const File &file) : mPath(file.getPath()) {
}


FileOutputStream::~FileOutputStream() {
    close();
}

bool FileOutputStream::open() {
    return openFileInternal(false);
}

bool FileOutputStream::open(bool append) {
    return openFileInternal(append);
}

bool FileOutputStream::openFileInternal(bool append) {
    char *mode;
    if (append) {
        // 追加数据
        mode = "a";
    } else {
        // 创建新文件
        mode = "w+";
    }
    mFile = fopen(mPath.c_str(), mode);
    return mFile != 0;
}

// TODO:不处理写入数据失败的情况
void FileOutputStream::write(const std::string &str) {
    // 写入数据
    ::fwrite(str.data(), 1, str.length(), mFile);
}

void FileOutputStream::write(const char *data, size_t len) {
    ::fwrite(data, 1, len, mFile);
}

void FileOutputStream::close() {
    if (mFile != 0) {
        ::fclose(mFile);
        mFile = 0;
    }
}