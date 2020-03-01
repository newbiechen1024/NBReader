// author : newbiechen
// date : 2020/3/1 10:05 PM
// description : 
//

#ifndef NBREADER_STYLECLOSETAG_H
#define NBREADER_STYLECLOSETAG_H


#include "TextTag.h"

class StyleCloseTag: public TextTag {
public:
    StyleCloseTag();

protected:
    virtual void writeToParcelInternal(Parcel &parcel);
};


#endif //NBREADER_STYLECLOSETAG_H
