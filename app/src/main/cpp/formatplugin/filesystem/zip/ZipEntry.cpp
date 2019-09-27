// author : newbiechen
// date : 2019-09-25 16:35
// description : 
//

#include <filesystem/File.h>
#include <util/AndroidUtil.h>
#include "ZipEntry.h"
#include "ZipItemHeader.h"

ZipEntry::ZipEntry(const std::string &path) : mPath(path) {
    File zipFile = File(path);
    mLastModifiedTime = zipFile.lastModified();

    std::shared_ptr<InputStream> inputStream = zipFile.getInputStream();
    // 如果输入流打开失败
    if (!inputStream->open()) {
        return;
    }
    ZipItemHeader itemHeader;
    // 从 stream 中循环读取一个 ZipItemHeader
    while (ZipHeaderDetector::readItemHeader(*inputStream, itemHeader)) {
        ZipItemInfo *infoPtr = nullptr;
        // 通过读取 stream 获取 zip 信息
        if (itemHeader.signature == (unsigned long) ZipItemHeader::SIGNATURE_LOCAL_FILE) {
            // 初始化 string
            std::string itemName(itemHeader.nameLength, '\0');
            // 读取 item name
            if ((unsigned int) inputStream->read((char *) itemName.data(), itemHeader.nameLength) ==
                itemHeader.nameLength) {
                // todo ==> 一定要经过 jstring 去处理吗，直接获取的文本有问题?
                itemName = AndroidUtil::convertNonUtfString(itemName);
                // 从 map 中获取 itemName，如果 name 不存在则会创建一个默认的 itemInfo
                // 获取这个默认 itemInfo 的引用
                ZipItemInfo &info = mItemMap[itemName];

                info.offset = inputStream->offset() + itemHeader.extraLength;
                info.compressionMethod = itemHeader.compressionMethod;
                info.compressedSize = itemHeader.compressedSize;
                info.uncompressedSize = itemHeader.uncompressedSize;
                infoPtr = &info;
            }
        }
        ZipHeaderDetector::skipItemInfo(*inputStream, itemHeader);
        if (infoPtr != 0) {
            infoPtr->uncompressedSize = itemHeader.uncompressedSize;
        }
    }
    // 关闭流
    inputStream->close();
}

void ZipEntry::readItemNames(std::vector<std::string> &names) const {
    for (std::map<std::string, ZipItemInfo>::const_iterator it = mItemMap.begin();
         it != mItemMap.end(); ++it) {
        names.push_back(it->first);
    }
}

ZipItemInfo ZipEntry::getItemInfo(const std::string &itemName) const {
    std::map<std::string, ZipItemInfo>::const_iterator it = mItemMap.find(itemName);
    return (it != mItemMap.end()) ? it->second : ZipItemInfo();
}

bool ZipEntry::isValid() const {
    return mLastModifiedTime == File(mPath).lastModified();
}