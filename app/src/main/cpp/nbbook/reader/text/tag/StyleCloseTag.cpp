// author : newbiechen
// date : 2020/3/1 10:05 PM
// description : 
//

#include "StyleCloseTag.h"

StyleCloseTag::StyleCloseTag() : TextTag(TextTagType::STYLE_CLOSE) {
}

void StyleCloseTag::writeToParcelInternal(Parcel &parcel) {
    // 不实现
}
