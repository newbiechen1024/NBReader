// author : newbiechen
// date : 2020-02-20 21:38
// description : 图片资源
//

#ifndef NBREADER_IMAGETAG_H
#define NBREADER_IMAGETAG_H


#include <string>
#include "TextTag.h"
#include "../../../tools/drm/FileEncryptionInfo.h"

class ImageTag : public TextTag {
public:
    ImageTag(const std::string &path, const std::string &encoding, bool isCover, short vOffset,
              size_t offset, size_t size, std::shared_ptr<EncryptionMap> encryptionInfo)
            : path(path), encoding(encoding), encryptionInfo(encryptionInfo) {
        this->isCover = isCover;
        this->vOffset = vOffset;
        this->offset = offset;
        this->size = size;
    }

    // 图片地址
    const std::string path;
    // 图片的编码方式
    const std::string encoding;
    // 是否是丰满
    bool isCover;
    // 偏移(暂时不知道这个东西有什么作用)
    short vOffset;
    // 文件偏移
    size_t offset;
    // 图片大小
    size_t size;
    // 图片加密信息
    std::shared_ptr<EncryptionMap> encryptionInfo;
};


#endif //NBREADER_IMAGETAG_H
