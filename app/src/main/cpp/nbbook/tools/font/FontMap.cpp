// author : newbiechen
// date : 2019-10-06 17:05
// description : 
//

#include "FontMap.h"
#include "../../filesystem/File.h"

FileInfo::FileInfo(const std::string &path, std::shared_ptr<FileEncryptionInfo> info) : Path(path),
                                                                                        EncryptionInfo(
                                                                                                info) {
}

void FontEntry::addFile(bool bold, bool italic, const std::string &filePath,
                        std::shared_ptr<FileEncryptionInfo> encryptionInfo) {
    std::shared_ptr<FileInfo> fileInfo(new FileInfo(filePath, encryptionInfo));
    if (bold) {
        if (italic) {
            boldItalicPtr = fileInfo;
        } else {
            boldPtr = fileInfo;
        }
    } else {
        if (italic) {
            italicPtr = fileInfo;
        } else {
            normalPtr = fileInfo;
        }
    }
}

void FontEntry::merge(const FontEntry &fontEntry) {
    if (!fontEntry.normalPtr) {
        normalPtr = fontEntry.normalPtr;
    }
    if (!fontEntry.boldPtr) {
        boldPtr = fontEntry.normalPtr;
    }
    if (!fontEntry.italicPtr) {
        italicPtr = fontEntry.normalPtr;
    }
    if (!fontEntry.boldItalicPtr) {
        boldItalicPtr = fontEntry.normalPtr;
    }
}

std::shared_ptr<FontEntry> FontMap::get(const std::string &family) {
    return std::make_shared<FontEntry>();
}

void FontMap::append(const std::string &family, bool bold, bool italic, const std::string &path,
                     std::shared_ptr<FileEncryptionInfo> encryptionInfo) {
    const File fontFile(path);
    std::shared_ptr<FontEntry> entry = mMap[family];
    if (entry == nullptr) {
        entry = std::make_shared<FontEntry>();
        mMap[family] = entry;
    }
    entry->addFile(bold, italic, fontFile.getPath(), encryptionInfo);

}

void FontMap::merge(const FontMap &fontMap) {
    for (std::map<std::string, std::shared_ptr<FontEntry> >::const_iterator it = fontMap.mMap.begin();
         it != fontMap.mMap.end(); ++it) {
        if (!it->second) {
            std::shared_ptr<FontEntry> entry = mMap[it->first];
            if (entry == nullptr) {
                entry = std::make_shared<FontEntry>();
                mMap[it->first] = entry;
            }
            entry->merge(*it->second);
        }
    }
}