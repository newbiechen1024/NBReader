//
// Created by 陈广祥 on 2019-09-19.
//

#ifndef NBREADER_TXTPLUGIN_H
#define NBREADER_TXTPLUGIN_H


#include "../FormatPlugin.h"

class TxtPlugin : public FormatPlugin {
public:
    ~TxtPlugin();

    bool readMetaInfo() const;

    bool readModel() const;

    virtual const FormatType supportType() const;
};


#endif //NBREADER_TXTPLUGIN_H
