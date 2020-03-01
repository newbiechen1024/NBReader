// author : newbiechen
// date : 2020-02-15 20:46
// description : 层叠样式标签
//

#ifndef NBREADER_STYLETAG_H
#define NBREADER_STYLETAG_H


#include "TextTag.h"
#include <string>
#include <vector>
#include "../type/TextStyleTagType.h"
#include "../type/TextAlignmentType.h"
#include "../../../util/Boolean.h"
#include "../type/TextTagType.h"
#include "../../../util/CommonUtil.h"

class StyleTag : public TextTag {

public:
    struct Metrics {
        Metrics(int fontSize, int fontXHeight, int fullWidth, int fullHeight);

        int FontSize;
        int FontXHeight;
        int FullWidth;
        int FullHeight;
    };

private:
    struct LengthType {
        TextSizeUnit Unit;
        short Size;
    };

public:
    StyleTag(TextTagType styleType);

    //StyleTag(unsigned char entryKind, char *address);
    ~StyleTag();

    TextTagType entryKind() const;

    bool isEmpty() const;

    bool isFeatureSupported(TextFeature featureId) const;

    //short length(TextFeature featureId, const Metrics &metrics) const;
    void setLength(TextFeature featureId, short length, TextSizeUnit unit);

    TextAlignmentType alignmentType() const;

    void setAlignmentType(TextAlignmentType alignmentType);

    Boolean fontModifier(TextFontModifier modifier) const;

    void setFontModifier(TextFontModifier modifier, bool on);

    const std::vector<std::string> &fontFamilies() const;

    void setFontFamilies(const std::vector<std::string> &fontFamilies);

    unsigned char verticalAlignCode() const;

    void setVerticalAlignCode(unsigned char code);

    TextDisplayCode displayCode() const;

    void setDisplayCode(TextDisplayCode code);

    void setDepth(unsigned depth) const {
        mDepth = depth;
    }

    std::shared_ptr<StyleTag> start() const;

    std::shared_ptr<StyleTag> end() const;

    std::shared_ptr<StyleTag> inherited() const;


protected:
    void writeToParcelInternal(Parcel &parcel) const override;

private:
    const TextTagType myEntryKind;
    unsigned short myFeatureMask;

    LengthType myLengths[(char) TextFeature::NUMBER_OF_LENGTHS];
    TextAlignmentType myAlignmentType;
    unsigned char mySupportedFontModifier;
    unsigned char myFontModifier;
    std::vector<std::string> myFontFamilies;
    unsigned char myVerticalAlignCode;
    TextDisplayCode myDisplayCode;
    // TODO:深度是我自己加的不知道有没有问题，可能存在 Style 被多次利用，且深度不同的可能。(暂时先这样，等以后理解了再说)
    mutable unsigned char mDepth;
};

inline StyleTag::StyleTag(TextTagType styleType) : TextTag(styleType),
                                                   myEntryKind(styleType),
                                                   myFeatureMask(0),
                                                   mySupportedFontModifier(0),
                                                   myFontModifier(0),
                                                   myAlignmentType(
                                                           TextAlignmentType::ALIGN_UNDEFINED),
                                                   myDisplayCode(TextDisplayCode::DC_NOT_DEFINED),
                                                   mDepth(0) {}

inline StyleTag::~StyleTag() {}

inline TextTagType StyleTag::entryKind() const { return myEntryKind; }

inline StyleTag::Metrics::Metrics(int fontSize, int fontXHeight, int fullWidth,
                                  int fullHeight) : FontSize(fontSize),
                                                    FontXHeight(fontXHeight),
                                                    FullWidth(fullWidth),
                                                    FullHeight(fullHeight) {}

inline bool StyleTag::isEmpty() const { return myFeatureMask == 0; }

inline bool StyleTag::isFeatureSupported(TextFeature featureId) const {
    return (myFeatureMask & (1 << CommonUtil::to_underlying(featureId))) != 0;
}

inline void
StyleTag::setLength(TextFeature featureId, short length, TextSizeUnit unit) {
    auto featureResult = CommonUtil::to_underlying(featureId);

    myFeatureMask |= 1 << featureResult;
    myLengths[featureResult].Size = length;
    myLengths[featureResult].Unit = unit;
}

inline TextAlignmentType StyleTag::alignmentType() const { return myAlignmentType; }

inline void StyleTag::setAlignmentType(TextAlignmentType alignmentType) {
    myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::ALIGNMENT_TYPE);
    myAlignmentType = alignmentType;
}

inline Boolean StyleTag::fontModifier(TextFontModifier modifier) const {
    auto modifierResult = CommonUtil::to_underlying(modifier);

    if ((mySupportedFontModifier & modifierResult) == 0) {
        return Boolean::UNDEFINED;
    }

    return (myFontModifier & modifierResult) == 0 ? Boolean::FALSE : Boolean::TRUE;
}

inline void StyleTag::setFontModifier(TextFontModifier modifier, bool on) {
    auto modifierResult = CommonUtil::to_underlying(modifier);
    myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::FONT_STYLE_MODIFIER);
    mySupportedFontModifier |= modifierResult;
    if (on) {
        myFontModifier |= modifierResult;
    } else {
        myFontModifier &= ~modifierResult;
    }
}

inline const std::vector<std::string> &
StyleTag::fontFamilies() const { return myFontFamilies; }

inline void StyleTag::setFontFamilies(const std::vector<std::string> &fontFamilies) {
    if (!fontFamilies.empty()) {
        myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::FONT_FAMILY);
        myFontFamilies = fontFamilies;
    }
}

inline unsigned char StyleTag::verticalAlignCode() const { return myVerticalAlignCode; }

inline void StyleTag::setVerticalAlignCode(unsigned char code) {
    myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::NON_LENGTH_VERTICAL_ALIGN);
    myVerticalAlignCode = code;
}

inline TextDisplayCode
StyleTag::displayCode() const { return myDisplayCode; }

inline void StyleTag::setDisplayCode(TextDisplayCode code) {
    if (code != TextDisplayCode::DC_NOT_DEFINED) {
        myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::DISPLAY);
        myDisplayCode = code;
    }
}


#endif //NBREADER_STYLETAG_H
