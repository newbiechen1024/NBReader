// author : newbiechen
// date : 2019-09-20 15:11
// description : 
// TODO:没有处理 FontManager ==> 以后再说

#ifndef NBREADER_TEXTMODEL_H
#define NBREADER_TEXTMODEL_H

#include <string>
#include <map>
#include <jni.h>
#include <tools/font/FontManager.h>
#include "TextCachedAllocator.h"
#include "TextParagraph.h"
#include "TextStyleEntry.h"
#include "TextEntry.h"

typedef unsigned char HyperlinkType;
typedef unsigned char TextMark;

class VideoEntry;

class TextModel {
public:
    virtual ~TextModel();

    const std::string &id() const;

    const std::string &language() const;
    //bool isRtl() const;

    std::size_t paragraphsNumber() const;

    TextParagraph *operator[](std::size_t index);

    const TextParagraph *operator[](std::size_t index) const;

    void addControl(TextMark textMark, bool isStart);

    void addStyleEntry(const TextStyleEntry &entry, unsigned char depth);

    void addStyleEntry(const TextStyleEntry &entry, const std::vector<std::string> &fontFamilies,
                       unsigned char depth);

    void addStyleCloseEntry();

    void addHyperlinkControl(TextMark textMark, HyperlinkType hyperlinkType,
                             const std::string &label);

    void addText(const std::string &text);

    void addTextArray(const std::vector<std::string> &text);

    void addImage(const std::string &id, short vOffset, bool isCover);

    void addFixedHSpace(unsigned char length);

    void addBidiReset();

    void addVideoEntry(const VideoEntry &entry);

    void addExtensionEntry(const std::string &action, const std::map<std::string, std::string> &data);

    void flush();

    const TextCachedAllocator &allocator() const;

    const std::vector<jint> &startEntryIndices() const;

    const std::vector<jint> &startEntryOffsets() const;

    const std::vector<jint> &paragraphLengths() const;

    const std::vector<jint> &textSizes() const;

    const std::vector<jbyte> &paragraphKinds() const;

protected:
    TextModel(const std::string &id, const std::string &language, const std::size_t rowSize,
              const std::string &directoryName, const std::string &fileExtension, FontManager &fontManager);

    TextModel(const std::string &id, const std::string &language,
              std::shared_ptr<TextCachedAllocator> allocator, FontManager &fontManager);

    void addParagraphInternal(TextParagraph *paragraph);

private:
    const std::string mId;
    const std::string mLanguage;
    std::vector<TextParagraph *> mParagraphs;
    mutable std::shared_ptr<TextCachedAllocator> mAllocator;

    char *mLastEntryStart;

    std::vector<jint> mStartEntryIndices;
    std::vector<jint> mStartEntryOffsets;
    std::vector<jint> mParagraphLengths;
    std::vector<jint> mTextSizes;
    std::vector<jbyte> mParagraphKinds;

    FontManager &mFontManager;
};


#endif //NBREADER_TEXTMODEL_H
