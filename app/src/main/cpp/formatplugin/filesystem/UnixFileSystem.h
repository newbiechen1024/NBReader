// author : newbiechen
// date : 2019-09-23 10:59
// description : 
//

#ifndef NBREADER_UNIXFILESYSTEM_H
#define NBREADER_UNIXFILESYSTEM_H


#include "FileSystem.h"

class UnixFileSystem : public FileSystem {
public:
    // 对其他 path 进行标准化
    virtual std::string normalizePath(const std::string &path) const;

protected:
    virtual void normalizeInternal(std::string &path) const;

    // 创建目录
    virtual bool createDirectory(const std::string &path) const;

    // 创建并返回目录
    virtual FileDir *getDirectory(const std::string &path) const;

    // 创建文件
    virtual bool createFile(const std::string &path) const;

    // 删除文件
    virtual bool deleteFile(const std::string &path) const;

    // 获取文件状态信息
    virtual FileStat getFileStat(const std::string &path) const;
};


#endif //NBREADER_UNIXFILESYSTEM_H
