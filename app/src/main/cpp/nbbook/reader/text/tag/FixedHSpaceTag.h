// author : newbiechen
// date : 2020/3/1 8:26 PM
// description : 修复竖直区域标签
//

#ifndef NBREADER_FIXEDHSPACETAG_H
#define NBREADER_FIXEDHSPACETAG_H


#include "TextTag.h"

class FixedHSpaceTag : public TextTag {
public:
    FixedHSpaceTag(unsigned char length);

protected:
    void writeToParcelInternal(Parcel &parcel) const override;

private:
    unsigned char length;
};


#endif //NBREADER_FIXEDHSPACETAG_H
