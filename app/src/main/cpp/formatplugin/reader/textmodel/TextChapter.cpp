// author : newbiechen
// date : 2019-12-09 15:50
// description : 
//

#include "TextChapter.h"


TextChapter::TextChapter() : title(nullptr) {
    startIndex = 0;
    endIndex = 0;
}

TextChapter::TextChapter(const std::string &chapterTitle, size_t startIndex, size_t endIndex)
        : title(chapterTitle), startIndex(startIndex), endIndex(endIndex) {

}