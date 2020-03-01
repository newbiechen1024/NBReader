// author : newbiechen
// date : 2019-12-30 16:17
// description : 
//

#include "BookEncoder.h"
#include "../text/type/TextResType.h"
#include "../../util/Logger.h"
#include "../../util/StringUtil.h"
#include "../text/tag/ControlTag.h"
#include "../text/tag/FixedHSpaceTag.h"
#include "../text/tag/StyleCloseTag.h"

static const size_t BUFFER_SIZE = 4096;

BookEncoder::BookEncoder() {
    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;
    isEnterTitle = false;
    hasOpen = false;
    mIdGenerator = 0;

    mParcelBuffer = nullptr;
    mResParcel = nullptr;
}

BookEncoder::~BookEncoder() {
    release();
}


void BookEncoder::open() {
    // 如果已经打开了，则不处理
    if (isOpen()) {
        return;
    }
    // 启动文件编码器
    mTextEncoder.open();
    // 启动资源信息分配器
    mParcelBuffer = new ParcelBuffer(BUFFER_SIZE);
    mResParcel = new Parcel(mParcelBuffer);
}

TextContent BookEncoder::close() {
    char *resourcePtr = nullptr;
    char *contentPtr = nullptr;

    size_t resourceSize = mParcelBuffer->flush(&resourcePtr);
    size_t contentSize = mTextEncoder.close(&contentPtr);

    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;
    hasOpen = false;

    mParagraphTextList.clear();
    mTextKindStack.clear();

    // 释放
    release();

    return TextContent(resourcePtr, resourceSize, contentPtr, contentSize);
}

void BookEncoder::release() {
    if (mResParcel != nullptr) {
        delete mResParcel;
        mResParcel = nullptr;
    }

    if (mParcelBuffer != nullptr) {
        delete mParcelBuffer;
        mParcelBuffer = nullptr;
    }
}

void BookEncoder::beginParagraph(TextParagraph::Type type) {
    // 结束当前段落
    endParagraph();

    // 创建段落
    mTextEncoder.beginParagraph(type);

    // 将所有的 style 作为标记添加到新创建的段落中
    for (TextKind &textKind : mTextKindStack) {
        ControlTag tag(textKind, true);
        mTextEncoder.addTextTag(tag);
    }

    // 标记当前段落正在处理
    isParagraphOpen = true;
}

void BookEncoder::addText(const std::string &text) {
    if (text.empty() || !hasParagraphOpen()) {
        return;
    }

    // 如果正在输入标题，则非纯文本内容
    if (!isEnterTitle) {
        isSectionContainsRegularContents = true;
    }

    // 添加段落文本到段落缓冲区
    mParagraphTextList.push_back(text);
}

void BookEncoder::endParagraph() {
    // 如果段落未开启，则不需要结束
    if (!hasParagraphOpen()) {
        return;
    }
    // 输出段落缓冲到实际段落中
    flushParagraphBuffer();
    isParagraphOpen = false;
}

void BookEncoder::flushParagraphBuffer() {
    // 将当前获取到的文本添加到 TextModel 中
    mTextEncoder.addText(mParagraphTextList);
    mParagraphTextList.clear();
}

/**
 * 多级标题定义：
 *
 * 一级标题：
 *    -- 二级标题
 *    -- 二级标题
 * 一级标题

 * @param paragraphIndex
 */
void BookEncoder::beginTitleParagraph(int paragraphIndex) {
    isTitleParagraphOpen = true;
    // TODO:为实现多级标题而准备的，暂时不准备实现 (当前只支持一级标题)
}

void BookEncoder::addTitleText(const std::string &text) {
    if (!hasTitleParagraphOpen() || text.empty()) {
        return;
    }

    // TODO:为实现多级标题而准备的，暂时不准备实现(当前只支持一级标题)
}

void BookEncoder::endTitleParagraph() {
    // 标记标题段落结束
    isTitleParagraphOpen = false;

    // TODO:为实现多级标题而准备的，暂时不准备实现(当前只支持一级标题)
}

/**
 * 添加文本样式
 * @param type ：文本样式类型
 */
void BookEncoder::pushTextKind(TextKind kind) {
    mTextKindStack.push_back(kind);
}

/**
 * 删除文本样式
 * @return
 */
bool BookEncoder::popTextKind() {
    if (!mTextKindStack.empty()) {
        mTextKindStack.pop_back();
        return true;
    }
    return false;
}

void BookEncoder::insertEndOfSectionParagraph() {
    insertEndParagraph(TextParagraph::END_OF_SECTION_PARAGRAPH);
}

// 插入结束段落，保证 section 与文本不结合在一起
void BookEncoder::insertEndParagraph(TextParagraph::Type type) {
    // 如果片段是标准的内容
    if (isSectionContainsRegularContents) {
        TextParagraph *paragraph = mTextEncoder.getCurParagraph();
        if (paragraph != nullptr && paragraph->type != type) {
            endParagraph();

            // 通知  TextModel 创建新段落
            mTextEncoder.beginParagraph(type);
            isSectionContainsRegularContents = false;
        }
    }
}

void BookEncoder::addControlTag(TextKind kind, bool isStart) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        ControlTag tag(kind, isStart);
        mTextEncoder.addTextTag(tag);
    }

    // 超链接处理
/*    if (!isStart && !myHyperlinkReference.empty() && (kind == myHyperlinkKind)) {
        myHyperlinkReference.erase();
    }*/
}

void BookEncoder::addFixedHSpaceTag(unsigned char length) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        FixedHSpaceTag tag(length);
        mTextEncoder.addTextTag(tag);
    }
}

void BookEncoder::addStyleTag(const StyleTag &tag,
                              const std::vector<std::string> &fontFamilies, unsigned char depth) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        // TODO:暂时不处理 fontFamilies 和 depth
        // mTextEncoder.addStyleTag(tag, fontFamilies, depth);
        tag.setDepth(depth);
        mTextEncoder.addTextTag(tag);
    }
}

void BookEncoder::addStyleTag(const StyleTag &tag, unsigned char depth) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        // mTextEncoder.addStyleTag(tag, depth);
        // TODO:由于 tag 没有 depth，强制用了个这么个方法，之后理解了再修改。
        tag.setDepth(depth);

        mTextEncoder.addTextTag(tag);
    }
}

void BookEncoder::addStyleCloseTag() {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        StyleCloseTag tag;
        mTextEncoder.addTextTag(tag);
    }
}


// TODO:要换个名字，这个不好听
void BookEncoder::addInnerLabelTag(const std::string &label) {
    // 获取当前的段落索引数
/*    int paragraphNumber = mTextEncoder.paragraphsNumber();
    if (hasParagraphOpen()) {
        --paragraphNumber;
    }
    addInnerLabelTag(label, paragraphNumber);*/
}

void BookEncoder::addInnerLabelTag(const std::string &label, int paragraphNumber) {
    // TODO:看样子是要将资源返回，之后实现
/*    myModel.myInternalHyperlinks.insert(std::make_pair(
            label, BookModel::Label(myCurrentTextModel, paragraphNumber)
    ));*/
}

void BookEncoder::addHyperlinkControlTag(TextKind kind, const std::string &label) {
/*    myHyperlinkKind = kind;
    std::string type;
    switch (myHyperlinkKind) {
        case TextKind::INTERNAL_HYPERLINK:
            myHyperlinkType = HYPERLINK_INTERNAL;
            type = "internal";
            break;
        case TextKind::FOOTNOTE:
            myHyperlinkType = HYPERLINK_FOOTNOTE;
            type = "footnote";
            break;
        case TextKind::EXTERNAL_HYPERLINK:
            myHyperlinkType = HYPERLINK_EXTERNAL;
            type = "external";
            break;
        default:
            myHyperlinkType = HYPERLINK_NONE;
            break;
    }
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addHyperlinkControlTag(kind, myHyperlinkType, label);
    }
    myHyperlinkReference = label;*/
}

void BookEncoder::addVideoTag(const VideoTag &entry) {
/*    mySectionContainsRegularContents = true;
    endParagraph();
    beginParagraph();
    myCurrentTextModel->addVideoEntry(entry);
    endParagraph();*/
}

void BookEncoder::addExtensionTag(const std::string &action,
                                  const std::map<std::string, std::string> &data) {
/*    myCurrentTextModel->addExtensionEntry(action, data);*/
}

void BookEncoder::addImageTag(const ImageTag &tag) {
    isSectionContainsRegularContents = true;

    // 写入图片资源信息
    ImageResource resource = tag.imageResource;
    resource.writeToParcel(*mResParcel);

    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        // 添加图片标签
        mTextEncoder.addTextTag(tag);
    } else {
        beginParagraph();
        ControlTag beforeTag(TextKind::IMAGE, true);
        ControlTag afterTag(TextKind::IMAGE, false);

        // 添加图片标签控制位
        mTextEncoder.addTextTag(beforeTag);
        mTextEncoder.addTextTag(tag);
        mTextEncoder.addTextTag(afterTag);

        // 结束段落
        endParagraph();
    }
}

std::string
BookEncoder::addFontTag(const std::string &family, std::shared_ptr<FontEntry> fontEntry) {
    // TODO:到底名字要不要叫 resource 呢？先不想了
}

void BookEncoder::insertPseudoEndOfSectionParagraph() {
    insertEndParagraph(TextParagraph::PSEUDO_END_OF_SECTION_PARAGRAPH);
}