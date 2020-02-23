// author : newbiechen
// date : 2019-09-22 20:14
// description : 文件系统抽象类
// 暂不处理：压缩文件的问题

#ifndef NBREADER_FILESYSTEM_H
#define NBREADER_FILESYSTEM_H

#include <string>
#include "FileStat.h"
#include "FileDir.h"
#include "File.h"
#include <cstdio>

class File;

class FileSystem {
public:
    static const std::string separator;
    static const std::string archiveSeparator;

    static FileSystem &getInstance();

    static void deleteInstance();

    // 对其他 path 进行标准化
    virtual std::string normalizePath(const std::string &path) const = 0;

    File::ArchiveType getForceArchiveFile(const std::string &path) {
        auto findIt = mForceArchiveCache.find(path);
        if (findIt != mForceArchiveCache.end()) {
            return findIt->second;
        }
        return File::ArchiveType::NONE;
    }

protected:
    static FileSystem *sInstance;

    FileSystem();

    ~FileSystem();

    // 对当前 path 进行标准化
    void normalize(std::string &path);

    virtual void normalizeInternal(std::string &path) const = 0;

    // 创建目录
    virtual bool createDirectory(const std::string &path) const = 0;

    // 创建并返回目录
    virtual std::shared_ptr<FileDir> getDirectory(const std::string &path) const = 0;

    // 创建文件
    virtual bool createFile(const std::string &path) const = 0;

    // 删除文件
    virtual bool deleteFile(const std::string &path) const = 0;

    // 获取文件状态信息
    virtual FileStat getFileStat(const std::string &path) const = 0;

    int findArchiveNameDelimiter(const std::string &path) const;

    int findLastNameDelimiter(const std::string &path) const;

private:

    void addForceArchiveFile(const std::string &path, File::ArchiveType type) {
        mForceArchiveCache.insert(std::make_pair(path, type));
    }

    // 强制转换为 archive 文件的缓存信息。
    // TODO:没有想出更好的解决方案，本身需要记录文件地址强转为压缩包的信息。放到文件系统里面缓存还说的过去
    std::map<std::string, File::ArchiveType> mForceArchiveCache;

    friend class File;

    friend class FileDir;

};

inline FileSystem &FileSystem::getInstance() {
    return *sInstance;
}

inline FileSystem::FileSystem() {
}

inline FileSystem::~FileSystem() {
}

#endif //NBREADER_FILESYSTEM_H
