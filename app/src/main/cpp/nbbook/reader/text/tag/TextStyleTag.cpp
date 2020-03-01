// author : newbiechen
// date : 2020-02-15 20:46
// description : 
//

#include "TextStyleTag.h"

std::shared_ptr<TextStyleTag> TextStyleTag::start() const {
    std::shared_ptr<TextStyleTag> clone(new TextStyleTag(myEntryKind));
    clone->myFeatureMask =
            myFeatureMask & ~(1 << CommonUtil::to_underlying(TextFeature::LENGTH_SPACE_AFTER));
    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        clone->myLengths[i] = myLengths[i];
    }
    clone->myAlignmentType = myAlignmentType;
    clone->mySupportedFontModifier = mySupportedFontModifier;
    clone->myFontModifier = myFontModifier;
    clone->myFontFamilies = myFontFamilies;
    clone->myVerticalAlignCode = myVerticalAlignCode;
    return clone;
}

std::shared_ptr<TextStyleTag> TextStyleTag::end() const {
    auto lengthSpaceAfter = CommonUtil::to_underlying(TextFeature::LENGTH_SPACE_AFTER);
    if ((myFeatureMask & (1 << lengthSpaceAfter)) == 0) {
        return 0;
    }

    std::shared_ptr<TextStyleTag> clone(new TextStyleTag(myEntryKind));
    clone->myFeatureMask = 1 << lengthSpaceAfter;
    clone->myLengths[lengthSpaceAfter] = myLengths[lengthSpaceAfter];

    return clone;
}

std::shared_ptr<TextStyleTag> TextStyleTag::inherited() const {
    std::shared_ptr<TextStyleTag> clone(new TextStyleTag(myEntryKind));
    static const unsigned short skip =
            //(1 << LENGTH_MARGIN_LEFT) |
            //(1 << LENGTH_MARGIN_RIGHT) |
            //(1 << LENGTH_PADDING_LEFT) |
            //(1 << LENGTH_PADDING_RIGHT) |
            (1 << CommonUtil::to_underlying(TextFeature::LENGTH_SPACE_BEFORE)) |
            (1 << CommonUtil::to_underlying(TextFeature::LENGTH_SPACE_AFTER));
    clone->myFeatureMask = myFeatureMask & ~skip;
    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        clone->myLengths[i] = myLengths[i];
    }

    clone->myAlignmentType = myAlignmentType;
    clone->mySupportedFontModifier = mySupportedFontModifier;
    clone->myFontModifier = myFontModifier;
    clone->myFontFamilies = myFontFamilies;
    clone->myVerticalAlignCode = myVerticalAlignCode;
    return clone;
}

void TextStyleTag::writeToParcelInternal(Parcel &parcel) const {
    // 设置style 的深度
    parcel.writeInt8(mDepth);
    // 具有的功能类型
    parcel.writeInt16(myFeatureMask);

    for (int i = 0; i < CommonUtil::to_underlying(TextFeature::NUMBER_OF_LENGTHS); ++i) {
        if (isFeatureSupported((TextFeature) i)) {
            const TextStyleTag::LengthType &len = myLengths[i];
            parcel.writeInt16(len.Size);
            parcel.writeInt8((char) len.Unit);
        }
    }

    if (isFeatureSupported(TextFeature::ALIGNMENT_TYPE) ||
        isFeatureSupported(TextFeature::NON_LENGTH_VERTICAL_ALIGN)) {
        parcel.writeInt8((char) myAlignmentType);
        parcel.writeInt8((char) myVerticalAlignCode);
    }


    // TODO:暂时不处理字体信息，设置使用的 family 在资源文件中的索引

/*    if (tag.isFeatureSupported(TextFeature::FONT_FAMILY)) {

        address = ParcelBuffer::writeUInt16(address,

                                            myFontManager.familyListIndex(fontFamilies)
        0);
    }

    if (tag.isFeatureSupported(TextFeature::FONT_STYLE_MODIFIER)) {
        *address++ = tag.mySupportedFontModifier;
        *address = tag.myFontModifier;
    }*/
}