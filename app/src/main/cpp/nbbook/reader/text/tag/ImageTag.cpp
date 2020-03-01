// author : newbiechen
// date : 2020-02-20 21:38
// description : 
//

#include "ImageTag.h"

// TODO:ImageTag 应该分成 resource 和 tag

ImageTag::ImageTag(const std::string &path, const std::string &encoding,
                   bool isCover, short vOffset, size_t offset, size_t size,
                   std::shared_ptr<EncryptionMap> encryptionInfo) : TextTag(TextTagType::IMAGE),
                                                                    path(path), encoding(encoding),
                                                                    encryptionInfo(encryptionInfo) {
    this->isCover = isCover;
    this->vOffset = vOffset;
    this->offset = offset;
    this->size = size;
}

void ImageTag::writeToParcelInternal(Parcel &parcel) {

}
