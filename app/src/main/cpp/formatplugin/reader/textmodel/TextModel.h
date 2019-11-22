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
#include <reader/textmodel/tag/TextStyleTag.h>
#include <reader/textmodel/tag/NBTagStyle.h>
#include <reader/textmodel/tag/NBTagHyperlinkType.h>
#include "TextCachedAllocator.h"
#include "TextParagraph.h"
#include "TextStyleEntry.h"

class TextVideoTag;

class TextModel {
public:
    virtual ~TextModel();

    TextParagraph *operator[](size_t index) {
        return mParagraphs[std::min(mParagraphs.size() - 1, index)];
    }

    const TextParagraph *operator[](size_t index) const {
        return mParagraphs[std::min(mParagraphs.size() - 1, index)];
    }

    const std::string &id() const {
        return mId;
    }

    const std::string &language() const {
        return mLanguage;
    }

    size_t getParagraphCount() const {
        return mParagraphs.size();
    }

    const TextCachedAllocator &allocator() const {
        return *mPghDetailAllocator;
    }

    /**
     * 添加控制位标签
     * @param style:   NBReader 具有的文本样式类型
     * @param isTagStart
     */
    void addControlTag(NBTagStyle style, bool isTagStart);

    /**
     * 样式标签
     * @param entry: 文本样式标签，(从文本的 css 中提取的类型)
     * @param depth
     */
    void addStyleTag(const TextStyleTag &entry, unsigned char depth);

    void addStyleTag(const TextStyleTag &entry, const std::vector<std::string> &fontFamilies,
                     unsigned char depth);

    /**
     * 样式关闭标签
     */
    void addStyleCloseTag();

    /**
     * 超链接控制标签
     * @param textStyle
     * @param hyperlinkType
     * @param label
     */
    void addHyperlinkControlTag(NBTagStyle textStyle, NBTagHyperlinkType hyperlinkType,
                                const std::string &label);

    void addTextTag(const std::string &text);

    void addTextTags(const std::vector<std::string> &text);

    void addImageTag(const std::string &id, short vOffset, bool isCover);

    void addFixedHSpaceTag(unsigned char length);

    void addVideoTag(const TextVideoTag &entry);

    void addExtensionTag(const std::string &action, const std::map<std::string, std::string> &data);

    void flush();

protected:
    TextModel(const std::string &id, const std::string &language, const std::size_t rowSize,
              const std::string &directoryName,
              const std::string &fileName,
              FontManager &fontManager);

    TextModel(const std::string &id, const std::string &language,
              std::shared_ptr<TextCachedAllocator> pghBaseAllocator,
              std::shared_ptr<TextCachedAllocator> pghDetailAllocator,
            FontManager &fontManager);

    void addParagraphInternal(TextParagraph *paragraph);

private:
    const std::string mId;
    const std::string mLanguage;
    std::vector<TextParagraph *> mParagraphs;
    // 段落基础数据分配器
    mutable std::shared_ptr<TextCachedAllocator> mPghBaseAllocator;
    // 段落详情数据分配器
    mutable std::shared_ptr<TextCachedAllocator> mPghDetailAllocator;

    // 当前在缓冲区上创建的 entry 指针位置
    char *mCurEntryPointer;
    FontManager &mFontManager;
};

// 纯文本 model
class TextPlainModel : public TextModel {
public:
    TextPlainModel(const std::string &id, const std::string &language,
                   const std::size_t defaultBufferSize,
                   const std::string &directoryName,
                   const std::string &fileName,
                   FontManager &fontManager);

    TextPlainModel(const std::string &id, const std::string &language,
                   std::shared_ptr<TextCachedAllocator> pghBaseAllocator,
                   std::shared_ptr<TextCachedAllocator> pghDetailAllocator,
                   FontManager &fontManager);

    ~TextPlainModel();

    void createParagraph(TextParagraph::Type type);
};

#endif //NBREADER_TEXTMODEL_H
