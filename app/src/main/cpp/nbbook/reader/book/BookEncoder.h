// author : newbiechen
// date : 2019-12-30 16:17
// description : 书籍编码器
//

#ifndef NBREADER_BOOKENCODER_H
#define NBREADER_BOOKENCODER_H

#include "../text/tag/TextKind.h"
#include "../text/entity/TextParagraph.h"
#include "../text/TextEncoder.h"
#include "../text/tag/StyleTag.h"
#include "../../tools/font/FontMap.h"
#include "../text/tag/VideoTag.h"
#include "../text/tag/ImageTag.h"
#include "../text/entity/TextContent.h"
#include "../text/resource/ImageResource.h"
#include <string>
#include <vector>
#include <stack>

class BookEncoder {
public:
    BookEncoder();

    ~BookEncoder();

    bool isOpen() {
        return hasOpen;
    }

    void open();

    TextContent close();

    // 标签样式标记入栈
    void pushTextKind(TextKind kind);

    // 标签样式标记出栈
    bool popTextKind();

    // 开始处理段落，传入参数指定段落段落
    void beginParagraph(TextParagraph::Type type = TextParagraph::TEXT_PARAGRAPH);

    // 结束段落处理
    void endParagraph();

    // 标记书籍正在输入标题
    void enterTitle() {
        isEnterTitle = true;
    }

    // 标记书籍输入标题结束
    void exitTitle() {
        isEnterTitle = false;
    }

    // 添加文本数据
    void addText(const std::string &text);

    // 添加标题文本
    void addTitleText(const std::string &text);

    // 插入片段结束段落
    void insertEndOfSectionParagraph();

    // 启动标题段落
    void beginTitleParagraph(int paragraphIndex = -1);

    // 结束标题段落
    void endTitleParagraph();

    // 是否正在处理段落标签
    bool hasParagraphOpen() {
        return isParagraphOpen;
    }

    // 是否正在处理标题段落标签
    bool hasTitleParagraphOpen() {
        return isTitleParagraphOpen;
    }

    size_t getCurParagraphCount() {
        return mTextEncoder.getCurParagraphCount();
    }

    void insertPseudoEndOfSectionParagraph();

    /**
     * 控制标签
     * @param kind
     * @param isStart
     */
    void addControlTag(TextKind kind, bool isStart);

    /**
     * 间距修复标签
     * @param length
     */
    void addFixedHSpaceTag(unsigned char length);

    /**
     * 样式标签
     * @param tag
     * @param depth
     */
    void addStyleTag(const StyleTag &tag, unsigned char depth);

    void addStyleTag(const StyleTag &tag, const std::vector<std::string> &fontFamilies,
                     unsigned char depth);

    /**
     * 关闭样式标签
     */
    void addStyleCloseTag();

    /**
     * 添加内部标记资源
     * 用于处理内部超链接定位
     * @param label
     */
    void addInnerLabelTag(const std::string &label);

    void addInnerLabelTag(const std::string &label, int paragraphNumber);

    /**
     * 添加字体标签。
     * @param family
     * @param fontEntry
     * @return
     */
    std::string addFontTag(const std::string &family, std::shared_ptr<FontEntry> fontEntry);

    /**
     * 添加超链接标签
     * @param kind
     * @param label
     */
    void addHyperlinkControlTag(TextKind kind, const std::string &label);

    /**
     * 添加视频标签
     * @param tag
     */
    void addVideoTag(const VideoTag &tag);

    /**
     * 添加图片资源
     * @param tag
     */
    void addImageTag(const ImageTag &tag);

    // 添加扩展标签
    void
    addExtensionTag(const std::string &action, const std::map<std::string, std::string> &data);

private:
    void insertEndParagraph(TextParagraph::Type type);

    // 将段落缓冲输出到 textModel 中
    void flushParagraphBuffer();

    uint16_t generateResourceId() {
        return mIdGenerator++;
    }

    void release();

private:
    // 文本编码器
    TextEncoder mTextEncoder;
    // 资源信息分配器
    ParcelBuffer *mParcelBuffer;
    Parcel *mResParcel;
    // 文本样式栈
    std::vector<TextKind> mTextKindStack;
    // 段落文本列表
    std::vector<std::string> mParagraphTextList;
    // 资源映射表
    std::map<std::string, std::string> mResourceMap;
    // 是否已经存在打开的段落
    bool isParagraphOpen;
    bool isTitleParagraphOpen;
    // 是否区域包含纯文本内容
    bool isSectionContainsRegularContents;
    // 当前是否正在输入标题
    bool isEnterTitle;
    bool hasOpen;
    // id 生成器，用于生成资源 id
    uint16_t mIdGenerator;
};

#endif //NBREADER_BOOKENCODER_H
