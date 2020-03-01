// author : newbiechen
// date : 2020/3/1 8:26 PM
// description : 
//

#include "FixedHSpaceTag.h"

FixedHSpaceTag::FixedHSpaceTag(unsigned char length) : TextTag(TextTagType::FIXED_HSPACE) {
    this->length = length;
}

void FixedHSpaceTag::writeToParcelInternal(Parcel &parcel) {
    parcel.writeInt8(length);
}


