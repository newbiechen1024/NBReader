
// author : newbiechen
// date : 2020/3/1 9:45 PM
// description : 
//

#ifndef NBREADER_TEXTRESOURCE_H
#define NBREADER_TEXTRESOURCE_H


#include "../../../tools/parcel/Parcel.h"
#include "../type/TextResType.h"
#include "../../../util/CommonUtil.h"

class TextResource : public Parcelable {
protected:
    virtual void writeToParcelInternal(Parcel &parcel) const = 0;

public:
    TextResource(TextResType type) {
        mType = type;
    }

    virtual ~TextResource() {
    }


    TextResType getType() {
        return mType;
    }

    void writeToParcel(Parcel &parcel) const override {
        parcel.writeInt8(CommonUtil::to_underlying(mType));
        writeToParcelInternal(parcel);
    }

private:
    TextResType mType;
};


#endif //NBREADER_TEXTRESOURCE_H
