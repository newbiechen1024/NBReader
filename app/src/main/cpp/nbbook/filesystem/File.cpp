// author : newbiechen
// date : 2019-09-22 19:48
// description : 
// 1. 对于在压缩包内的文件路径，xxx.zip/path/file 不作为压缩文件，只有压缩包的才是压缩文件。
// 2. 如果 xxx.zip/path/file 是压缩包内的文件，获取文件信息的时候，会先获取 zip 压缩包，再从压缩包中认证该 file 的 FileStat 信息

#include "File.h"
#include "../util/StringUtil.h"
#include "../util/UnicodeUtil.h"
#include "zip/ZipInputStream.h"
#include "io/FileInputStream.h"
#include "zip/ZipFileDir.h"
#include "FileSystem.h"

const File File::NO_FILE;

static const std::string SUFFIX_ZIP = ".zip";
static const std::string SUFFIX_GZIP = ".gz";


File::File() : isInitFileStat(true) {
}

File::File(const std::string &path) : mPath(path), mName(""), mFullName(""),
                                      mExtension(""), mArchiveType(NONE) {
    isInitFileStat = false;

    // 标准化地址
    FileSystem::getInstance().normalize(mPath);

    if (mPath.empty()) {
        return;
    }

    // 查找文件名的前缀
    size_t lastNameIndex = FileSystem::getInstance().findLastNameDelimiter(mPath);

    // 如果 archive 尾缀存在，则删除
    if (lastNameIndex < mPath.length() - 1) {
        mFullName = mPath.substr(lastNameIndex + 1);
    } else {
        mFullName = mPath;
    }

    mName = mFullName;

    // 从缓存中判断该 path 是否被强转
    ArchiveType forceFileType = FileSystem::getInstance().getForceArchiveFile(mPath);

    if (forceFileType != NONE) {
        mArchiveType = forceFileType;
    } else {
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
        // TODO:返回的是压缩文件的压缩包信息，不是实际的压缩文件信息。因为获取压缩文件信息，每次都需要对压缩包进行解析处理，太麻烦了。
        // TODO:这个之后的想一个办法解决。
        mFileStat = archive.getFileStat();
        // todo：这个 size 能不能是单独文件的 size？
        mFileStat.isDirectory = false;
        mFileStat.exists = false;
        std::vector<std::string> items;
        dir->readFileNames(items);

        // 如果 zip 目录中存在的文件与当前 ":" 对应的文件名相同
        for (auto it = items.begin(); it != items.end(); ++it) {
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

const std::string File::getArchivePkgPath() const {
    const int index = FileSystem::getInstance().findArchiveNameDelimiter(mPath);
    if (index == -1) {
        return std::string();
    }
    return mPath.substr(0, index);
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

void File::forceArchiveType(ArchiveType type) const {
    mArchiveType = type;
    // 将强转为压缩文件的路径添加到缓存中
    FileSystem::getInstance().addForceArchiveFile(mPath, type);
}

// TODO:暂时不处理加密相关逻辑
std::shared_ptr<InputStream>
File::getInputStream(std::shared_ptr<EncryptionMap> encryptionMap) const {
    // TODO:未处理加密事件

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
        if (base != nullptr) {
            // 如果是 zip 压缩格式
            if (baseFile.mArchiveType & ZIP) {
                std::shared_ptr<ZipInputStream> zipInputStream(
                        new ZipInputStream(base, baseName, mPath.substr(index + 1)));
                stream = zipInputStream;
            } else {
                if (isDirectory()) {
                    return 0;
                }
                stream = std::make_shared<FileInputStream>(mPath);
            }
        }
    }
    return stream;
}

std::shared_ptr<FileOutputStream> File::getOutputStream() const {
    // 如果是压缩文件、或者是目录，直接返回
    if (isArchive() || isCompressed() || isDirectory()) {
        return nullptr;
    }
    return std::make_shared<FileOutputStream>(mPath);
}

