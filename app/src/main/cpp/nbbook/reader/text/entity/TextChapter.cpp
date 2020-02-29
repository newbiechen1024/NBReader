// author : newbiechen
// date : 2019-12-09 15:50
// description : 
//

#include "TextChapter.h"

TextChapter::TextChapter() : url(""), title("") {
    startIndex = 0;
    endIndex = 0;
}

TextChapter::TextChapter(const std::string &url, const std::string &title, int startIndex,
                         int endIndex) : url(url), title(title),
                                            startIndex(startIndex), endIndex(endIndex) {

}