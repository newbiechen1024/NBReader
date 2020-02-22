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
}