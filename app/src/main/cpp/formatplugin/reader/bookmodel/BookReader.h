// author : newbiechen
// date : 2019-09-27 17:41
// description : 为 TextModel 文本添加对应的类型标记
//

#ifndef NBREADER_BOOKREADER_H
#define NBREADER_BOOKREADER_H


#include "BookModel.h"
#include "NBTextMark.h"

class BookReader {
public:
    BookReader(BookModel &model);

    virtual ~BookReader();
};


#endif //NBREADER_BOOKREADER_H
