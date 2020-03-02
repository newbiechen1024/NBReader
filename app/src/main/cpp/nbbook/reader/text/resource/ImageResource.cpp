// author : newbiechen
// date : 2020/3/1 9:45 PM
// description : 
//

#include "ImageResource.h"
#include "../../../util/Logger.h"

ImageResource::ImageResource(const std::string &resId, const std::string &path,
                             const std::string &encoding, short vOffset,
                             size_t offset, size_t size,
                             std::shared_ptr<EncryptionMap> encryptionInfo)
        : TextResource(TextResType::IMAGE), resId(resId),
          path(path), encoding(encoding), encryptionInfo(encryptionInfo) {

    this->vOffset = vOffset;
    this->offset = offset;
    this->size = size;
}

void ImageResource::writeToParcelInternal(Parcel &parcel) const {
    // 写入 id
    parcel.writeString16(resId);
    // 写入路径
    parcel.writeString16(path);
}