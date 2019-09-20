//
// Created by 陈广祥 on 2019-09-19.
//

#include "TxtPlugin.h"

TxtPlugin::~TxtPlugin() {
}

bool TxtPlugin::readMetaInfo() const {
    return true;
}

bool TxtPlugin::readModel() const {
    return true;
}

const FormatType TxtPlugin::supportType() const {
    return TXT;
}
