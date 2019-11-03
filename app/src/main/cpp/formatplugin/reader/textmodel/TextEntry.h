// author : newbiechen
// date : 2019-09-27 18:15
// description :  段落块封装 ==> 段落块结构：| 段落类型 | 段落类型对应的数据信息


/**
 * entry 存储到本地文件的结构表：
 *
 * CONTROL_ENTRY：占用 4 字节，格式为 | entry 类型 | 未知类型 | 样式标签 | 是开放标签还是闭合标签 |
 *
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 样式标签：占用 1 字节 ==> 详见 TextParagraph::Type
 * 4. 标签类型：占用 1 字节 ==> 0 或者是
 *
 * TEXT_ENTRY：占用 (6 + 文本字节数) 格式为 | entry 类型 | 未知类型 | 文本字节长度 | 文本内容
 *
 * 1. entry 类型：占用 1 字节
 * 2. 未知类型：占用 1 字节 ==> 基本上为 0 好像没用过
 * 3. 文本字节长度：占用 4 字节
 * 4. 文本内容：占用文本长度字节。
 */

#ifndef NBREADER_TEXTENTRY_H
#define NBREADER_TEXTENTRY_H

#include <reader/bookmodel/NBTextStyle.h>
#include <memory>
#include <map>
#include <string>
#include <reader/bookmodel/NBHyperlinkType.h>

// 段落元素 ==> 基类
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
    virtual ~TextParagraphEntry() {
    }

private: // 禁止复制
    TextParagraphEntry(const TextParagraphEntry &entry);

    const TextParagraphEntry &operator=(const TextParagraphEntry &entry);
};

/*class TextControlEntry : public TextParagraphEntry {

protected:
    TextControlEntry(NBTextStyle mark, bool isFirstParagraph) : mMark(mark), hasStart(isFirstParagraph) {
    }

public:
    virtual ~ZLTextControlEntry() {
    }

    NBTextStyle getStyleTag() const {
        return mMark;
    }

    bool isFirstParagraph() const {
        return hasStart;
    }

    virtual bool isHyperlink() const {
        return false;
    }

private:
    NBTextStyle mMark;
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

    std::shared_ptr<TextParagraphEntry> getControlEntry(NBTextStyle mark, bool isFirstParagraph);

private:
    std::map<NBTextStyle, std::shared_ptr<TextParagraphEntry>> mStartEntries;
    std::map<NBTextStyle, std::shared_ptr<TextParagraphEntry>> mEndEntries;
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

#endif //NBREADER_TEXTENTRY_H
