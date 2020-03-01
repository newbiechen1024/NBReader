// author : newbiechen
// date : 2019-12-30 14:31
// description : 文本标签
//

#ifndef NBREADER_TEXTTAG_H
#define NBREADER_TEXTTAG_H


#include "../../../tools/parcel/Parcel.h"
#include "../type/TextTagType.h"
#include "../../../util/CommonUtil.h"

class TextTag : public Parcelable {
protected:
    virtual void writeToParcelInternal(Parcel &parcel) const = 0;

public:
    TextTag(TextTagType type) {
        mType = type;
    }

    TextTagType getType() {
        return mType;
    }

    virtual ~TextTag() {
    }

    void writeToParcel(Parcel &parcel) const override {
        parcel.writeInt8(CommonUtil::to_underlying(mType));
        writeToParcelInternal(parcel);
    }

private:
    TextTagType mType;
};

#endif //NBREADER_TEXTTAG_H
