// author : newbiechen
// date : 2019-10-06 16:56
// description : 
// TODO:暂未实现

#include "FontManager.h"

std::string FontManager::put(const std::string &family, std::shared_ptr<FontEntry> entry) {
    return std::string();
}

const std::map<std::string, std::shared_ptr<FontEntry> > &FontManager::entries() const {
    return std::map<std::string, std::shared_ptr<FontEntry> >();
}

int FontManager::familyListIndex(const std::vector<std::string> &familyList) {
    return 0;
}

/*const std::vector<std::vector<std::string> > &FontManager::familyLists() const {
    return std::vector<std::vector<std::string>>();
}*/

