// author : newbiechen
// date : 2020/2/28 10:18 PM
// description : 
//

#include "TextContent.h"

TextContent::TextContent() {
    contentPtr = nullptr;
    resourcePtr = nullptr;
    resourceSize = 0;
    contentSize = 0;

    hasInitialized = false;
}

TextContent::TextContent(char *resourcePtr, size_t resourceSize, char *contentPtr,
                         size_t contentSize) {
    this->resourcePtr = resourcePtr;
    this->resourceSize = resourceSize;

    this->contentPtr = contentPtr;
    this->contentSize = contentSize;

    hasInitialized = true;
}

void TextContent::release() {
    if (contentPtr != nullptr) {
        delete contentPtr;
        contentPtr = nullptr;
    }

    if (resourcePtr != nullptr) {
        delete resourcePtr;
    }

    contentSize = 0;
    resourceSize = 0;

    hasInitialized = false;
}