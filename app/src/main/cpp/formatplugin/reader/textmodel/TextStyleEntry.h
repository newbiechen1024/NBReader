// author : newbiechen
// date : 2019-10-06 16:41
// description : 文本样式元素
//

#ifndef NBREADER_TEXTSTYLEENTRY_H
#define NBREADER_TEXTSTYLEENTRY_H


#include "TextEntry.h"
#include "TextModel.h"
#include "TextAlignmentType.h"

class TextStyleEntry: TextParagraphEntry {
public:
    enum SizeUnit {
        SIZE_UNIT_PIXEL,
        SIZE_UNIT_POINT,
        SIZE_UNIT_EM_100,
        SIZE_UNIT_REM_100,
        SIZE_UNIT_EX_100,
        SIZE_UNIT_PERCENT
    };

    struct Metrics {
        Metrics(int fontSize, int fontXHeight, int fullWidth, int fullHeight);

        int FontSize;
        int FontXHeight;
        int FullWidth;
        int FullHeight;
    };

    enum FontModifier {
        FONT_MODIFIER_BOLD =           1 << 0,
        FONT_MODIFIER_ITALIC =         1 << 1,
        FONT_MODIFIER_UNDERLINED =     1 << 2,
        FONT_MODIFIER_STRIKEDTHROUGH = 1 << 3,
        FONT_MODIFIER_SMALLCAPS =      1 << 4,
        FONT_MODIFIER_INHERIT =        1 << 5,
        FONT_MODIFIER_SMALLER =        1 << 6,
        FONT_MODIFIER_LARGER =         1 << 7,
    };

    enum Feature {
        LENGTH_PADDING_LEFT =               0,
        LENGTH_PADDING_RIGHT =              1,
        LENGTH_MARGIN_LEFT =                2,
        LENGTH_MARGIN_RIGHT =               3,
        LENGTH_FIRST_LINE_INDENT =          4,
        LENGTH_SPACE_BEFORE =               5,
        LENGTH_SPACE_AFTER =                6,
        LENGTH_FONT_SIZE =                  7,
        LENGTH_VERTICAL_ALIGN =             8,
        NUMBER_OF_LENGTHS =                 9,
        ALIGNMENT_TYPE =                    NUMBER_OF_LENGTHS,
        FONT_FAMILY =                       NUMBER_OF_LENGTHS + 1,
        FONT_STYLE_MODIFIER =               NUMBER_OF_LENGTHS + 2,
        NON_LENGTH_VERTICAL_ALIGN =         NUMBER_OF_LENGTHS + 3,
        DISPLAY =                           NUMBER_OF_LENGTHS + 4 // 11; max = 15
    };

    enum DisplayCode {
        DC_NOT_DEFINED = -1,
        DC_INLINE,
        DC_BLOCK,
        DC_FLEX,
        DC_INLINE_BLOCK,
        DC_INLINE_FLEX,
        DC_INLINE_TABLE,
        DC_LIST_ITEM,
        DC_RUN_IN,
        DC_TABLE,
        DC_TABLE_CAPTION,
        DC_TABLE_COLUMN_GROUP,
        DC_TABLE_HEADER_GROUP,
        DC_TABLE_FOOTER_GROUP,
        DC_TABLE_ROW_GROUP,
        DC_TABLE_CELL,
        DC_TABLE_COLUMN,
        DC_TABLE_ROW,
        DC_NONE,
        DC_INITIAL,
        DC_INHERIT
    };

private:
    struct LengthType {
        SizeUnit Unit;
        short Size;
    };

public:
    TextStyleEntry(unsigned char textMark);
    
    ~TextStyleEntry();

    unsigned char getMark() const;
    
    bool isEmpty() const;
    bool isFeatureSupported(Feature featureId) const;

    void setLength(Feature featureId, short length, SizeUnit unit);

    TextAlignmentType getAlignmentType() const;
    void setAlignmentType(TextAlignmentType alignmentType);
    // TODO：我觉得暂时先不处理
    Boolean3 hasFontModifier(FontModifier modifier) const;
    void setFontModifier(FontModifier modifier, bool on);

    const std::vector<std::string> &getFontFamilies() const;
    void setFontFamilies(const std::vector<std::string> &fontFamilies);

    unsigned char getVerticalAlignCode() const;
    void setVerticalAlignCode(unsigned char code);

    DisplayCode getDisplayCode() const;
    void setDisplayCode(DisplayCode code);

    std::shared_ptr<TextStyleEntry> start() const;
    std::shared_ptr<TextStyleEntry> end() const;
    std::shared_ptr<TextStyleEntry> inherited() const;

private:
    const unsigned char mTextMark;
    unsigned short mFeatureMask;
    LengthType mLengths[NUMBER_OF_LENGTHS];
    TextAlignmentType mAlignmentType;
    unsigned char mSupportedFontModifier;
    unsigned char mFontModifier;
    std::vector<std::string> mFontFamilies;
    unsigned char mVerticalAlignCode;
    DisplayCode mDisplayCode;

    friend class TextModel;
};


#endif //NBREADER_TEXTSTYLEENTRY_H
