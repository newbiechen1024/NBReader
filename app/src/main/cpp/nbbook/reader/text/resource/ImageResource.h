// author : newbiechen
// date : 2020/3/1 9:45 PM
// description : 图片资源
//

#ifndef NBREADER_IMAGERESOURCE_H
#define NBREADER_IMAGERESOURCE_H

#include "TextResource.h"
#include "../../../tools/drm/FileEncryptionInfo.h"

class ImageResource : public TextResource {

public:
    ImageResource(const std::string &resId, const std::string &path,
                  const std::string &encoding, short vOffset,
                  size_t offset, size_t size, std::shared_ptr<EncryptionMap> encryptionInfo);

protected:
    void writeToParcelInternal(Parcel &parcel) const override;

private:
    const std::string resId;
    // 图片地址
    const std::string path;
    // 图片的编码方式
    const std::string encoding;
    // 偏移(暂时不知道这个东西有什么作用)
    short vOffset;
    // 文件偏移
    size_t offset;
    // 图片大小
    size_t size;
    // 图片加密信息
    std::shared_ptr<EncryptionMap> encryptionInfo;
};

#endif //NBREADER_IMAGERESOURCE_H
