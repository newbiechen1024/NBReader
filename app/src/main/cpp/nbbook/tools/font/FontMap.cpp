// author : newbiechen
// date : 2019-10-06 17:05
// description : 
//

#include "FontMap.h"
std::shared_ptr<FontEntry> FontMap::get(const std::string &family) {
    return std::make_shared<FontEntry>();
}

void FontMap::append(const std::string &family, bool bold, bool italic, const std::string &path) {

}

void FontMap::merge(const FontMap &fontMap) {

}