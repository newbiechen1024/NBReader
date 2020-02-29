// author : newbiechen
// date : 2020/2/28 10:18 PM
// description : 文本内容
//

#ifndef NBREADER_TEXTCONTENT_H
#define NBREADER_TEXTCONTENT_H


#include <cstdlib>

class TextContent {

public:
    char *resourcePtr;
    size_t resourceSize;
    char *contentPtr;
    size_t contentSize;

    TextContent();

    TextContent(char *resourcePtr, size_t resourceSize,char *contentPtr, size_t contentSize);

    bool isInitialized() {
        return hasInitialized;
    }

    void release();

private:
    bool hasInitialized;
};


#endif //NBREADER_TEXTCONTENT_H
