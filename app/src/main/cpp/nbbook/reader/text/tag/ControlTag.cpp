// author : newbiechen
// date : 2020/3/1 8:23 PM
// description : 
//

#include "ControlTag.h"

ControlTag::ControlTag(TextKind kind, bool isStartTag) : TextTag(TextTagType::CONTROL) {
    this->kind = kind;
    this->isStartTag = isStartTag;
}

void ControlTag::writeToParcelInternal(Parcel &parcel) const {
    auto value = CommonUtil::to_underlying(kind);
    parcel.writeInt8(value);
    parcel.writeBool(isStartTag);
}