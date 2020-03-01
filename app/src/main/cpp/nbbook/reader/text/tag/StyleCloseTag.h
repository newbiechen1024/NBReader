// author : newbiechen
// date : 2020/3/1 10:05 PM
// description : 
//

#ifndef NBREADER_STYLECLOSETAG_H
#define NBREADER_STYLECLOSETAG_H


#include "TextTag.h"

class StyleCloseTag : public TextTag {
public:
    StyleCloseTag();

protected:
    void writeToParcelInternal(Parcel &parcel) const override;
};


#endif //NBREADER_STYLECLOSETAG_H
