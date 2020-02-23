// author : newbiechen
// date : 2020-02-23 11:42
// description : 
//

#ifndef NBREADER_OEBREADER_H
#define NBREADER_OEBREADER_H

#include <string>
#include "../../reader/text/entity/TextChapter.h"
#include "../../reader/book/BookEncoder.h"
#include "../../tools/xhtml/XHTMLReader.h"

class OebReader {
public:
    OebReader() {
    }

    ~OebReader() {
    }

    // 读取章节信息
    size_t readContent(TextChapter &chapter, char **outBuffer);

private:
    // 书籍编码器
    BookEncoder mBookEncoder;

    XHTMLReader mXhtmlReader;

};


#endif //NBREADER_OEBREADER_H
