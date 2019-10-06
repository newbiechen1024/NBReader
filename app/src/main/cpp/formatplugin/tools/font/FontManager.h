// author : newbiechen
// date : 2019-10-06 16:56
// description : 字体管理器
//

#ifndef NBREADER_FONTMANAGER_H
#define NBREADER_FONTMANAGER_H

#include <string>
class FontManager {

public:
    std::string put(const std::string &family, shared_ptr<FontEntry> entry);
    int familyListIndex(const std::vector<std::string> &familyList);

    const std::map<std::string,shared_ptr<FontEntry> > &entries() const;
    const std::vector<std::vector<std::string> > &familyLists() const;

private:
    std::map<std::string,shared_ptr<FontEntry> > myEntries;
    std::vector<std::vector<std::string> > myFamilyLists;
};

#endif //NBREADER_FONTMANAGER_H
