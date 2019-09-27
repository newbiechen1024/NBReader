// author : newbiechen
// date : 2019-09-22 19:48
// description : 
//

#ifndef NBREADER_FILE_H
#define NBREADER_FILE_H

#include <string>
#include <stddef.h>
#include <filesystem/io/InputStream.h>
#include <filesystem/io/OutputStream.h>
#include "FileStat.h"
#include "FileDir.h"

class File {
public:
    // 压缩类型
    enum ArchiveType {
        NONE = 0,
        GZIP = 0x0001,
        ZIP = 0x0100,
        COMPRESSED = 0x00ff,
        ARCHIVE = 0xff00,
    };

    static const std::string SUFFIX_ZIP = ".zip";
    static const std::string SUFFIX_GZIP = ".gz";

    File(const std::string &path);

    ~File() {}

    // 获取文件名,如：xx/xx/file.txt ，返回 file
    std::string &getName() const {
        return mName;
    }

    // 获取完整名称，如：xx/xx/file.txt ，返回 file.txt
    std::string &getFullName() const {
        return mFullName;
    }

    // 获取扩展名，如：xx/xx/file.txt ，返回 txt
    std::string &getExtension() const {
        return mExtension;
    }

    // 获取绝对路径
    std::string &getPath() const {
        return mPath;
    }

    // 文件是否存在
    bool exists() const {
        return mFileStat.exists;
    }

    // 判断是否是压缩文件
    bool isArchive() const {
        return (mArchiveType & ARCHIVE) != 0;
    }

    bool isCompressed() const {
        return (mArchiveType & COMPRESSED) != 0;
    }

    // 判断是否是目录
    bool isDirectory() const {
        return mFileStat.isDirectory;
    }

    // 判断是否是文件
    bool isFile() const {
        return !mFileStat.isDirectory;
    }

    // 最后的修改时间
    size_t lastModified() const {
        return mFileStat.lastModifiedTime;
    };

    // 文件的大小
    size_t length() const {
        return mFileStat.size;
    }

    // 创建文件
    bool createFile() const;

    // 删除文件
    bool deleteFile() const;

    // 创建整个文件目录
    bool mkdirs() const;

    // TODO:返回文件夹 ==> 返回的是指针，还是 shared\_ptr
    std::shared_ptr<FileDir> getDirectory() const;

    std::shared_ptr<InputStream> getInputStream() const;

    std::shared_ptr<OutputStream> getOutputStream() const;

    bool operator==(const File &other) const {
        return mPath == other.mPath;
    }

    bool operator!=(const File &other) const {
        return mPath != other.mPath;
    }

private:
    std::string mPath;
    std::string mName;
    std::string mFullName;
    std::string mExtension;
    FileStat mFileStat;
    ArchiveType mArchiveType;
    bool isInitFileStat;

    FileStat &getFileStat();
};

#endif //NBREADER_FILE_H
