// author : newbiechen
// date : 2019-09-27 18:15
// description : 文本包含的元素集合
//

#ifndef NBREADER_TEXTPARAGRAPH_H
#define NBREADER_TEXTPARAGRAPH_H

#include <reader/bookmodel/NBTextMark.h>
#include <memory>
#include <map>
#include <string>
#include <reader/bookmodel/NBHyperlinkType.h>

// 段落元素
class TextParagraphEntry {

public:
    // 段落元素类型
    enum Type {
        TEXT_ENTRY = 1, // 文本元素
        IMAGE_ENTRY = 2, // 图片元素
        CONTROL_ENTRY = 3, // 控制元素
        HYPERLINK_CONTROL_ENTRY = 4, // 超链接控制元素
        STYLE_CSS_ENTRY = 5,
        STYLE_OTHER_ENTRY = 6,
        STYLE_CLOSE_ENTRY = 7,
        FIXED_HSPACE_ENTRY = 8,
        RESET_BIDI_ENTRY = 9,
        AUDIO_ENTRY = 10,
        VIDEO_ENTRY = 11,
        EXTENSION_ENTRY = 12,
    };

protected:
    TextParagraphEntry() {
    }

public:
    virtual ~ZLTextParagraphEntry() {
    }

private: // 禁止复制
    TextParagraphEntry(const TextParagraphEntry &entry);

    const TextParagraphEntry &operator=(const TextParagraphEntry &entry);
};

/*class TextControlEntry : public TextParagraphEntry {

protected:
    TextControlEntry(NBTextMark mark, bool isStart) : mMark(mark), hasStart(isStart) {
    }

public:
    virtual ~ZLTextControlEntry() {
    }

    NBTextMark getMark() const {
        return mMark;
    }

    bool isStart() const {
        return hasStart;
    }

    virtual bool isHyperlink() const {
        return false;
    }

private:
    NBTextMark mMark;
    bool hasStart;

    friend class TextControlEntryPool;
};*/






/*class TextControlEntryPool {
public:
    static TextControlEntryPool sControlEntryPool;
public:
    TextControlEntryPool() {
    }

    ~ZLTextControlEntryPool() {
    }

    std::shared_ptr<TextParagraphEntry> getControlEntry(NBTextMark mark, bool isStart);

private:
    std::map<NBTextMark, std::shared_ptr<TextParagraphEntry>> mStartEntries;
    std::map<NBTextMark, std::shared_ptr<TextParagraphEntry>> mEndEntries;
};

class TextFixedHSpaceEntry : public TextParagraphEntry {

public:
    TextFixedHSpaceEntry(unsigned char length) : mLength(length) {
    }

    unsigned char length() const {
        return mLength;
    }

private:
    const unsigned char mLength;
};

class TextHyperlinkControlEntry : public TextControlEntry {

public:
    TextHyperlinkControlEntry();

    ~TextHyperlinkControlEntry() {
    }

    const std::string &getLabel() const {
        return mLabel;
    }

    NBHyperlinkType getHyperlinkType() const {
        return mHyperlinkType;
    }

    bool isHyperlink() const {
        return true;
    }

private:
    std::string mLabel;
    NBHyperlinkType mHyperlinkType;
};

class TextEntry : public TextParagraphEntry {

public:
    ~TextEntry() {
    }

    std::size_t dataLength() const {
        return mText.length();
    }

    const char *data() const {
        return mText.data();
    }

private:
    std::string mText;
};

class ImageEntry : public TextParagraphEntry {

public:
    ~ImageEntry() {
    }

    const std::string &getId() const {
        return mId;
    }

    short vOffset() const {
        return mVOffset;
    }

private:
    std::string mId;
    short mVOffset;
};

class ResetBidiEntry : public TextParagraphEntry {

public:
    static const std::shared_ptr<TextParagraphEntry> sInstance;

private:
    ResetBidiEntry() {
    }
};

class ExtensionEntry : public TextParagraphEntry {

public:
    ~ExtensionEntry() {
    }

    const std::string &getAction() const;

    const std::string &getData() const;

private:
    std::string mAction;
    std::string mData;
};*/


class VideoEntry : public TextParagraphEntry {

public:
    void addSource(const std::string &type, const std::string &path) {
        mSources.insert(std::make_pair(type, path));
    }

    const std::map<std::string, std::string> &getSources() const {
        return mSources;
    }

private:
    std::map<std::string, std::string> mSources;
};

#endif //NBREADER_TEXTPARAGRAPH_H
