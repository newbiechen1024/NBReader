// author : newbiechen
// date : 2019-10-06 17:05
// description : 
// TODO ==> FBReader 暂不处理 DRM 加密相关逻辑

#ifndef NBREADER_FONTMAP_H
#define NBREADER_FONTMAP_H

#include <string>
class FileInfo {

public:
    FileInfo(const std::string &path);

public:
    const std::string PATH;
};

class FontEntry {

public:
    void addFile(bool bold, bool italic, const std::string &filePath, shared_ptr<FileEncryptionInfo> encryptionInfo);
    void merge(const FontEntry &fontEntry);

    bool operator == (const FontEntry &other) const;
    bool operator != (const FontEntry &other) const;

public:
    shared_ptr<FileInfo> Normal;
    shared_ptr<FileInfo> Bold;
    shared_ptr<FileInfo> Italic;
    shared_ptr<FileInfo> BoldItalic;
};

class FontMap {

public:
    void append(const std::string &family, bool bold, bool italic, const std::string &path, shared_ptr<FileEncryptionInfo> encryptionInfo);
    void merge(const FontMap &fontMap);
    std::shared_ptr<FontEntry> get(const std::string &family);

private:
    std::map<std::string,shared_ptr<FontEntry> > myMap;
};

#endif //NBREADER_FONTMAP_H
