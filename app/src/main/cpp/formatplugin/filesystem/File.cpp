// author : newbiechen
// date : 2019-09-22 19:48
// description : 
//

#include "File.h"
#include "FileSystem.h"
#include <map>
#include <util/UnicodeUtil.h>
#include <util/StringUtil.h>

File::File(const std::string &path) : mPath(path) {
    // 标准化地址
    FileSystem::getInstance().normalize(mPath);
    // 查找文件名的前缀
    size_t lastNameIndex = FileSystem::getInstance().findLastNameDelimiter(mPath);

    // 如果 archive 尾缀存在，则删除
    if (lastNameIndex < mPath.length() - 1) {
        mFullName = mPath.substr(lastNameIndex + 1);
    } else {
        mFullName = mPath;
    }

    mName = mFullName;

    // 将文件名，转换为小写
    std::string lowerCaseName = UnicodeUtil::toLower(mFullName);

    // 判断是否是 gz
    if (StringUtil::endsWith(lowerCaseName, SUFFIX_GZIP)) {
        int endLength = SUFFIX_GZIP.length();
        mName = mFullName.substr(0, mFullName.length() - endLength);
        mArchiveType = mArchiveType | GZIP;

        lowerCaseName = lowerCaseName.substr(0, lowerCaseName.length() - endLength);
    }

    // 判断是否是 zip
    if (StringUtil::endsWith(lowerCaseName, SUFFIX_GZIP)) {
        mArchiveType = mArchiveType | ZIP;
    }

    // 如果是压缩格式，一般 extension 为 null
    // 如果仍然有扩展名，则进行分割
    int index = mName.rfind('.');
    if (index > 0) {
        mExtension = UnicodeUtil::toLower(mName.substr(index + 1));
        mName = mName.substr(0, index);
    }
}

FileStat &File::getFileStat() {
    if (isInitFileStat) {
        return mFileStat;
    }

    // 判断是否存在 archive 类型
    int index = FileSystem::getInstance().findArchiveNameDelimiter(mPath);
    // 如果不存在
    if (index == -1) {
        mFileStat = FileSystem::getInstance().getFileStat(mPath);
        return mFileStat;
    }

    // 压缩路径
    const std::string archivePath = mPath.substr(0, index);
    // 创建压缩文件
    File archive(archivePath);

    // 如果不是压缩文件
    if (!archive.isArchive()) {
        mFileStat = FileSystem::getInstance().getFileStat(mPath);
        return mFileStat;
    }

    // 如果压缩包不存在
    if (!archive.exists()) {
        mFileStat.exists = false;
        return mFileStat;
    }

    // 如果压缩包存在
    if (archive.exists()) {
        // TODO: 需要有一个 Dir
    }
    return mFileStat;
}

bool File::createFile() const {
    return FileSystem::getInstance().createFile(mPath);
}

bool File::deleteFile() const {
    return FileSystem::getInstance().deleteFile(mPath);
}

bool File::mkdirs() const {
    return FileSystem::getInstance().createDirectory(mPath);
}

std::shared_ptr<InputStream> File::getInputStream() const {

}

std::shared_ptr<OutputStream> File::getOutputStream() const {

}

