// author : newbiechen
// date : 2020/3/1 8:23 PM
// description : 控制位标签
//

#ifndef NBREADER_CONTROLTAG_H
#define NBREADER_CONTROLTAG_H


#include "TextTag.h"
#include "TextKind.h"

class ControlTag : public TextTag {
public:
    ControlTag(TextKind kind, bool isStartTag);

protected:
    void writeToParcelInternal(Parcel &parcel) const override;

private:
    TextKind kind;
    bool isStartTag;
};


#endif //NBREADER_CONTROLTAG_H
