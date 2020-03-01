// author : newbiechen
// date : 2020/3/1 9:49 PM
// description : 文本内容标签
//

#ifndef NBREADER_CONTENTTAG_H
#define NBREADER_CONTENTTAG_H


#include "TextTag.h"

class ContentTag : public TextTag {
public:
    ContentTag(const std::string &text);

    ContentTag(const std::vector<std::string> &texts);

private:
    std::vector<std::string> mContentList;
};


#endif //NBREADER_CONTENTTAG_H
