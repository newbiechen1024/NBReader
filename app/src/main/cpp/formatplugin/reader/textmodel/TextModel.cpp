// author : newbiechen
// date : 2019-09-20 15:11
// description : 
//

#include "TextModel.h"

TextModel::TextModel(const std::string &id, const std::string &language, const std::size_t rowSize,
                     const std::string &directoryName, const std::string &fileExtension, FontManager &fontManager)
        : mId(0), mLanguage(language.empty() ? Library::Language() : language),
          mAllocator(new TextCachedAllocator(rowSize, directoryName, fileExtension)),
          mLastEntryStart(0),
          mFontManager(fontManager) {

}

TextModel::TextModel(const std::string &id, const std::string &language, std::shared_ptr<TextCachedAllocator> allocator,
                     FontManager &fontManager) : mId(id),
                                                 mLanguage(language.empty() ? Library::Language() : language),
                                                 mAllocator(allocator),
                                                 mLastEntryStart(0),
                                                 mFontManager(fontManager) {

}

TextModel::~TextModel() {
    for (std::vector<TextParagraph *>::const_iterator it = mParagraphs.begin();
         it != mParagraphs.end(); ++it) {
        delete *it;
    }
}

void TextModel::addControl(TextStyle style, bool isStart) {

}

void TextModel::addStyleEntry(const TextStyleEntry &entry, unsigned char depth) {

}

void TextModel::addStyleEntry(const TextStyleEntry &entry, const std::vector<std::string> &fontFamilies,
                              unsigned char depth) {

}

void TextModel::addStyleCloseEntry() {

}

void TextModel::addHyperlinkControl(TextStyle textStyle, HyperlinkType hyperlinkType, const std::string &label) {

}

void TextModel::addText(const std::string &text) {

}

void TextModel::addTexts(const std::vector<std::string> &text) {

}

void TextModel::addImage(const std::string &id, short vOffset, bool isCover) {

}

void TextModel::addFixedHSpace(unsigned char length) {

}

void TextModel::addVideoEntry(const VideoEntry &entry) {

}

void TextModel::addExtensionEntry(const std::string &action, const std::map<std::string, std::string> &data) {

}

void TextModel::flush() {

}

void TextPlainModel::createParagraph(TextParagraph::Type type) {

}