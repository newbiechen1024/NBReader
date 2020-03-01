// author : newbiechen
// date : 2020/3/1 9:49 PM
// description : 
//

#include "ContentTag.h"

ContentTag::ContentTag(const std::string &text) : TextTag(TextTagType::TEXT) {
    mContentList.push_back(text);
}

ContentTag::ContentTag(const std::vector<std::string> &texts) : TextTag(TextTagType::TEXT) {
    mContentList.insert(mContentList.end(), texts.begin(), texts.end());
}

