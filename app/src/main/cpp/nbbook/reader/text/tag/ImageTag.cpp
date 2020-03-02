// author : newbiechen
// date : 2020-02-20 21:38
// description : 
//

#include "ImageTag.h"
#include "../../../filesystem/File.h"

// TODO:resId 暂时以 image name 为名。(没想好 id 怎么搞呢)

ImageTag::ImageTag(const std::string &resourceId, const std::string &path, const std::string &encoding,
                   bool isCover, short vOffset, size_t offset, size_t size,
                   std::shared_ptr<EncryptionMap> encryptionInfo) : TextTag(TextTagType::IMAGE),
                                                                    resId(resourceId),
                                                                    imageResource(resourceId, path,
                                                                                  encoding,
                                                                                  vOffset,
                                                                                  offset, size,
                                                                                  encryptionInfo) {
    this->isCover = isCover;
}

void ImageTag::writeToParcelInternal(Parcel &parcel) const {
    parcel.writeString16(resId);
    parcel.writeBool(isCover);
}
