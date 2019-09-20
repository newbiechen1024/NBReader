//
// Created by 陈广祥 on 2019-09-18.
//

#ifndef NBREADER_FORMATPLUGIN_H
#define NBREADER_FORMATPLUGIN_H

#include <string>
#include "FormatType.h"

class FormatPlugin {
protected:
    FormatPlugin();

public:
    virtual ~FormatPlugin();

    virtual bool readMetaInfo() const = 0;

    virtual bool readModel() const = 0;

    virtual const FormatType supportType() const = 0;
};

inline FormatPlugin::FormatPlugin() {
}

inline FormatPlugin::~FormatPlugin() {
}


#endif //NBREADER_FORMATPLUGIN_H
