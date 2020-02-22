// author : newbiechen
// date : 2020-02-15 20:46
// description : 层叠样式标签
//

#ifndef NBREADER_TEXTSTYLETAG_H
#define NBREADER_TEXTSTYLETAG_H


#include "TextTag.h"
#include <string>
#include <vector>
#include "../type/TextStyleTagType.h"
#include "../type/TextAlignmentType.h"
#include "../../../util/Boolean.h"
#include "../type/TextTagType.h"
#include "../../../util/CommonUtil.h"

class TextStyleTag : public TextTag {

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
    TextStyleTag(TextTagType styleType);

    //TextStyleTag(unsigned char entryKind, char *address);
    ~TextStyleTag();

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

    std::shared_ptr<TextStyleTag> start() const;

    std::shared_ptr<TextStyleTag> end() const;

    std::shared_ptr<TextStyleTag> inherited() const;

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

    friend class TextEncoder;
};

inline TextStyleTag::TextStyleTag(TextTagType styleType) : myEntryKind(styleType),
                                                           myFeatureMask(0),
                                                           myAlignmentType(
                                                                   TextAlignmentType::ALIGN_UNDEFINED),
                                                           mySupportedFontModifier(0),
                                                           myFontModifier(0),
                                                           myDisplayCode(
                                                                   TextDisplayCode::DC_NOT_DEFINED) {}

inline TextStyleTag::~TextStyleTag() {}

inline TextTagType TextStyleTag::entryKind() const { return myEntryKind; }

inline TextStyleTag::Metrics::Metrics(int fontSize, int fontXHeight, int fullWidth,
                                      int fullHeight) : FontSize(fontSize),
                                                        FontXHeight(fontXHeight),
                                                        FullWidth(fullWidth),
                                                        FullHeight(fullHeight) {}

inline bool TextStyleTag::isEmpty() const { return myFeatureMask == 0; }

inline bool TextStyleTag::isFeatureSupported(TextFeature featureId) const {
    return (myFeatureMask & (1 << CommonUtil::to_underlying(featureId))) != 0;
}

inline void
TextStyleTag::setLength(TextFeature featureId, short length, TextSizeUnit unit) {
    auto featureResult = CommonUtil::to_underlying(featureId);

    myFeatureMask |= 1 << featureResult;
    myLengths[featureResult].Size = length;
    myLengths[featureResult].Unit = unit;
}

inline TextAlignmentType TextStyleTag::alignmentType() const { return myAlignmentType; }

inline void TextStyleTag::setAlignmentType(TextAlignmentType alignmentType) {
    myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::ALIGNMENT_TYPE);
    myAlignmentType = alignmentType;
}

inline Boolean TextStyleTag::fontModifier(TextFontModifier modifier) const {
    auto modifierResult = CommonUtil::to_underlying(modifier);

    if ((mySupportedFontModifier & modifierResult) == 0) {
        return Boolean::UNDEFINED;
    }

    return (myFontModifier & modifierResult) == 0 ? Boolean::FALSE : Boolean::TRUE;
}

inline void TextStyleTag::setFontModifier(TextFontModifier modifier, bool on) {
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
TextStyleTag::fontFamilies() const { return myFontFamilies; }

inline void TextStyleTag::setFontFamilies(const std::vector<std::string> &fontFamilies) {
    if (!fontFamilies.empty()) {
        myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::FONT_FAMILY);
        myFontFamilies = fontFamilies;
    }
}

inline unsigned char TextStyleTag::verticalAlignCode() const { return myVerticalAlignCode; }

inline void TextStyleTag::setVerticalAlignCode(unsigned char code) {
    myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::NON_LENGTH_VERTICAL_ALIGN);
    myVerticalAlignCode = code;
}

inline TextDisplayCode
TextStyleTag::displayCode() const { return myDisplayCode; }

inline void TextStyleTag::setDisplayCode(TextDisplayCode code) {
    if (code != TextDisplayCode::DC_NOT_DEFINED) {
        myFeatureMask |= 1 << CommonUtil::to_underlying(TextFeature::DISPLAY);
        myDisplayCode = code;
    }
}


#endif //NBREADER_TEXTSTYLETAG_H
