//
// Created by 陈广祥 on 2019-09-19.
//

#include "TxtPlugin.h"

TxtPlugin::TxtPlugin() {
}

TxtPlugin::~TxtPlugin() {
}

bool TxtPlugin::readMetaInfo(Book &book) const {
    return true;
}

bool TxtPlugin::readModel(BookModel &bookModel) const {
    return true;
}

const FormatType TxtPlugin::supportType() const {
    return TXT;
}
