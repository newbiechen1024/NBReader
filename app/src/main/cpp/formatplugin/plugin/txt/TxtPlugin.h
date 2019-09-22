//
// Created by 陈广祥 on 2019-09-19.
//

#ifndef NBREADER_TXTPLUGIN_H
#define NBREADER_TXTPLUGIN_H


#include <entity/book/Book.h>
#include <entity/bookmodel/BookModel.h>
#include "../FormatPlugin.h"

class TxtPlugin : public FormatPlugin {
public:
    TxtPlugin();

    ~TxtPlugin();

    bool readMetaInfo(Book &book) const;

    bool readModel(BookModel &bookModel) const;

    const FormatType supportType() const;
};

#endif //NBREADER_TXTPLUGIN_H
