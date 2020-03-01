// author : newbiechen
// date : 2020/3/1 8:23 PM
// description : 
//

#include "ParagraphTag.h"

ParagraphTag::ParagraphTag(TextParagraph::Type type) : TextTag(TextTagType::PARAGRAPH) {
    this->type = type;
}

void ParagraphTag::writeToParcelInternal(Parcel &parcel) {
    auto paragraphType = CommonUtil::to_underlying(type);
    parcel.writeInt8(paragraphType);
}


