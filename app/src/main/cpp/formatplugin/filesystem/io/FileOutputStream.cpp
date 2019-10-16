// author : newbiechen
// date : 2019-09-27 17:08
// description :
//

#include <sys/stat.h>
#include <util/Logger.h>
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
    mTempPath = mPath + ".XXXXXX" + '\0';
    mode_t currentMask = umask(S_IRWXO | S_IRWXG);
    int temporaryFileDescriptor = mkstemp(const_cast<char *>(mTempPath.data()));
    umask(currentMask);
    if (temporaryFileDescriptor == -1) {
        return false;
    }

    mFile = fdopen(temporaryFileDescriptor, "w+");
    return mFile != 0;
}

void FileOutputStream::write(const std::string &str) {
    if (::fwrite(str.data(), 1, str.length(), mFile) != (std::size_t) str.length()) {
        hasError = true;
    }
}

void FileOutputStream::write(const char *data, size_t len) {
    if (fwrite(data, 1, len, mFile) != len) {
        hasError = true;
    }
}

void FileOutputStream::close() {
    if (mFile != 0) {
        ::fclose(mFile);
        mFile = 0;
        if (!hasError) {
            rename(mTempPath.c_str(), mPath.c_str());
        }
    }
}