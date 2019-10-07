// author : newbiechen
// date : 2019-10-06 17:05
// description : 
// TODO ==> FBReader 暂不处理 DRM 加密相关逻辑

#ifndef NBREADER_FONTMAP_H
#define NBREADER_FONTMAP_H

#include <string>
#include <map>

class FontEntry {

public:
    void addFile(bool bold, bool italic, const std::string &filePath);

    void merge(const FontEntry &fontEntry);

    bool operator==(const FontEntry &other) const;

    bool operator!=(const FontEntry &other) const;

public:
    std::shared_ptr<std::string> normalArr;
    std::shared_ptr<std::string> boldArr;
    std::shared_ptr<std::string> italicArr;
    std::shared_ptr<std::string> boldItalicArr;
};

class FontMap {

public:
    void append(const std::string &family, bool bold, bool italic, const std::string &path);

    void merge(const FontMap &fontMap);

    std::shared_ptr<FontEntry> get(const std::string &family);

private:
    std::map<std::string, std::shared_ptr<FontEntry>> mMap;
};

#endif //NBREADER_FONTMAP_H
