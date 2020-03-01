// author : newbiechen
// date : 2020-02-20 21:38
// description : 图片标签
//

#ifndef NBREADER_IMAGETAG_H
#define NBREADER_IMAGETAG_H


#include <string>
#include "TextTag.h"
#include "../../../tools/drm/FileEncryptionInfo.h"
#include "../resource/ImageResource.h"

class ImageTag : public TextTag {
public:
    ImageTag(const std::string &path, const std::string &encoding, bool isCover, short vOffset,
             size_t offset, size_t size, std::shared_ptr<EncryptionMap> encryptionInfo);

protected:
    void writeToParcelInternal(Parcel &parcel) const override;

public:
    // 图片资源信息
    ImageResource imageResource;

    const std::string resId;
    // 是否是封面
    bool isCover;
};


#endif //NBREADER_IMAGETAG_H
