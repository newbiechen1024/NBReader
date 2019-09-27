// author : newbiechen
// date : 2019-09-25 16:35
// description : zip 元素
//

#ifndef NBREADER_ZIPENTRIY_H
#define NBREADER_ZIPENTRIY_H

#include <string>
#include <map>

// Zip 文件中的条目信息
struct ZipItemInfo {
    int offset = -1;
    int compressionMethod;
    int compressedSize;
    int uncompressedSize;

};

// zip 元素或 zip 文件
class ZipEntry {
public:
    ZipEntry(const std::string &path);

    ~ZipEntry() {
    }

    // 获取 zip 文件内的条目信息
    ZipItemInfo getItemInfo(const std::string &itemName) const;

    // 读取 zip 文件内的条目的名字
    void readItemNames(std::vector<std::string> &names) const;

    // 当前 Entry 文件是否过期
    bool isValid() const;

    std::string getPath() const {
        return mPath;
    }

    size_t getLastModifiedTime() const {
        return mLastModifiedTime;
    }

private:
    const std::string mPath;
    size_t mLastModifiedTime;
    std::map<std::string, ZipItemInfo> mItemMap;
};


#endif //NBREADER_ZIPENTRIY_H
