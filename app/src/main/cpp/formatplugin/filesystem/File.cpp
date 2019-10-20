// author : newbiechen
// date : 2019-09-22 19:48
// description : 
//

#include "File.h"
#include "FileSystem.h"
#include <util/UnicodeUtil.h>
#include <util/StringUtil.h>
#include <filesystem/io/FileInputStream.h>
#include <filesystem/zip/ZipInputStream.h>
#include <filesystem/io/FileOutputStream.h>
#include <filesystem/zip/ZipFileDir.h>
#include <util/Logger.h>

File::File(const std::string &path) : mPath(path), mArchiveType(NONE) {
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
        mArchiveType = static_cast<ArchiveType>(mArchiveType | GZIP);

        lowerCaseName = lowerCaseName.substr(0, lowerCaseName.length() - endLength);
    }

    // 判断是否是 zip
    if (StringUtil::endsWith(lowerCaseName, SUFFIX_ZIP)) {
        mArchiveType = static_cast<ArchiveType>(mArchiveType | ZIP);
    }

    // 如果是压缩格式，一般 extension 为 null
    // 如果仍然有扩展名，则进行分割
    int index = mName.rfind('.');
    if (index > 0) {
        mExtension = UnicodeUtil::toLower(mName.substr(index + 1));
        mName = mName.substr(0, index);
    }

    // 进行初始化 ==> 就这么叫吧...
    mFileStat = getFileStat();
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
    std::shared_ptr<FileDir> dir = archive.getDirectory();

    if (dir != nullptr) {
        std::string itemName = mPath.substr(index + 1);
        mFileStat = archive.getFileStat();
        mFileStat.isDirectory = false;
        mFileStat.exists = false;
        std::vector<std::string> items;
        dir->readFileNames(items);

        // 如果 zip 目录中存在的文件与当前":"对应的文件名相同
        for (std::vector<std::string>::const_iterator it = items.begin();
             it != items.end(); ++it) {
            if (*it == itemName) {
                mFileStat.exists = true;
                break;
            }
        }
    } else {
        mFileStat.exists = false;
    }
    isInitFileStat = true;

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

std::shared_ptr<FileDir> File::getDirectory() const {
    if (!exists()) {
        return nullptr;
    }

    // 如果是 zip
    if (mArchiveType & ZIP) {
        return std::dynamic_pointer_cast<FileDir>(std::make_shared<ZipFileDir>(mPath));
    } else if (isDirectory()) {
        return FileSystem::getInstance().getDirectory(mPath);
    } else {
        return nullptr;
    }

}

// TODO:暂时不处理加密相关逻辑
std::shared_ptr<InputStream> File::getInputStream() const {
    std::shared_ptr<InputStream> stream;

    int index = FileSystem::getInstance().findArchiveNameDelimiter(mPath);
    // 如果不是 zip
    if (index == -1) {
        // 如果是目录则直接退出
        if (isDirectory()) {
            return nullptr;
        }
        // 创建文件输入流
        stream = std::dynamic_pointer_cast<InputStream>(std::make_shared<FileInputStream>(mPath));
    } else {
        const std::string baseName = mPath.substr(0, index);
        const File baseFile(baseName);
        std::shared_ptr<InputStream> base = baseFile.getInputStream();
        if (base == nullptr) {
            // 如果是 zip 压缩格式
            if (baseFile.mArchiveType & ZIP) {
                std::shared_ptr<ZipInputStream> zipInputStream(
                        new ZipInputStream(base, baseName, mPath.substr(index + 1)));
                stream = std::dynamic_pointer_cast<InputStream>(zipInputStream);
            } else {
                if (isDirectory()) {
                    return 0;
                }
                stream = std::dynamic_pointer_cast<InputStream>(std::make_shared<FileInputStream>(mPath));
            }
        }
    }
    return stream;
}

std::shared_ptr<OutputStream> File::getOutputStream() const {
    // 如果是压缩文件、或者是目录，直接返回
    if (isArchive() || isCompressed() || isDirectory()) {
        return nullptr;
    }
    return std::dynamic_pointer_cast<OutputStream>(std::make_shared<FileOutputStream>(mPath));
}

