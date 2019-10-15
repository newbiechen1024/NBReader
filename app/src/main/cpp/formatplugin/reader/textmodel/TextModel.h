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

typedef unsigned char HyperlinkType;
typedef unsigned char TextStyle;

class VideoEntry;

class TextModel {
public:
    virtual ~TextModel();

    TextParagraph *operator[](std::size_t index) {
        return mParagraphs[std::min(mParagraphs.size() - 1, index)];
    }

    const TextParagraph *operator[](std::size_t index) const {
        return mParagraphs[std::min(mParagraphs.size() - 1, index)];

    }

    const std::string &id() const {
        return mId;
    }

    const std::string &language() const {
        return mLanguage;
    }

    std::size_t getParagraphCount() const {
        return mParagraphs.size();
    }

    const TextCachedAllocator &allocator() const {
        return *mAllocator;
    }

    void addControlEntry(TextStyle style, bool isTagStart);

    void addStyleEntry(const TextStyleEntry &entry, unsigned char depth);

    void addStyleEntry(const TextStyleEntry &entry, const std::vector<std::string> &fontFamilies,
                       unsigned char depth);

    void addStyleCloseEntry();

    void addHyperlinkControl(TextStyle textStyle, HyperlinkType hyperlinkType,
                             const std::string &label);

    void addText(const std::string &text);

    void addTexts(const std::vector<std::string> &text);

    void addImage(const std::string &id, short vOffset, bool isCover);

    void addFixedHSpace(unsigned char length);

    void addVideoEntry(const VideoEntry &entry);

    void addExtensionEntry(const std::string &action, const std::map<std::string, std::string> &data);

    void flush();

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
    // 当前在缓冲区上创建的 entry 指针位置
    char *mCurEntryPointer;
    FontManager &mFontManager;
};

// 纯文本 model
class TextPlainModel : public TextModel {
public:
    TextPlainModel(const std::string &id, const std::string &language, const std::size_t defaultBufferSize,
                   const std::string &directoryName, const std::string &fileExtension,
                   FontManager &fontManager);

    TextPlainModel(const std::string &id, const std::string &language,
                   std::shared_ptr<TextCachedAllocator> allocator, FontManager &fontManager);

    void createParagraph(TextParagraph::Type type);
};

#endif //NBREADER_TEXTMODEL_H
