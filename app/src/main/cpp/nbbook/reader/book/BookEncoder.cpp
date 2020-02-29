// author : newbiechen
// date : 2019-12-30 16:17
// description : 
//

#include "BookEncoder.h"
#include "../text/type/TextResType.h"
#include "../../util/Logger.h"

static const size_t BUFFER_SIZE = 4096;

BookEncoder::BookEncoder() {
    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;
    isEnterTitle = false;
    hasOpen = false;
    mIdGenerator = 0;
}

BookEncoder::~BookEncoder() {
    if (mResAllocator != nullptr) {
        delete mResAllocator;
        mResAllocator = nullptr;
    }
}

void BookEncoder::open() {
    // 如果已经打开了，则不处理
    if (isOpen()) {
        return;
    }
    // 启动文件编码器
    mTextEncoder.open();
    // 启动资源信息分配器
    mResAllocator = new TextBufferAllocator(BUFFER_SIZE);
}

TextContent BookEncoder::close() {
    char *resourcePtr = nullptr;
    char *contentPtr = nullptr;

    size_t resourceSize = mResAllocator->flush(&resourcePtr);
    size_t contentSize = mTextEncoder.close(&contentPtr);

    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;
    hasOpen = false;

    mParagraphTextList.clear();
    mTextKindStack.clear();

    // 释放
    if (mResAllocator != nullptr) {
        delete mResAllocator;
        mResAllocator = nullptr;
    }

    return TextContent(resourcePtr, resourceSize, contentPtr, contentSize);
}

void BookEncoder::beginParagraph(TextParagraph::Type type) {
    // 结束当前段落
    endParagraph();

    // 创建段落
    mTextEncoder.createParagraph(type);

    // 将所有的 style 作为标记添加到新创建的段落中
    for (TextKind &textStyle : mTextKindStack) {
        mTextEncoder.addControlTag(textStyle, true);
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
    mTextEncoder.addTextTag(mParagraphTextList);
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
            mTextEncoder.createParagraph(type);
            isSectionContainsRegularContents = false;
        }
    }
}

void BookEncoder::addControlTag(TextKind kind, bool isStart) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addControlTag(kind, isStart);
    }

    // 超链接处理
/*    if (!isStart && !myHyperlinkReference.empty() && (kind == myHyperlinkKind)) {
        myHyperlinkReference.erase();
    }*/
}

void BookEncoder::addFixedHSpaceTag(unsigned char length) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addFixedHSpace(length);
    }
}

void BookEncoder::addStyleTag(const TextStyleTag &tag,
                              const std::vector<std::string> &fontFamilies, unsigned char depth) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addStyleTag(tag, fontFamilies, depth);
    }
}

void BookEncoder::addStyleTag(const TextStyleTag &tag, unsigned char depth) {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addStyleTag(tag, depth);
    }
}

void BookEncoder::addStyleCloseTag() {
    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        mTextEncoder.addStyleCloseTag();
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
    // 添加图片资源，返回 id 映射
    uint16_t id = addImageResource(tag);

    if (hasParagraphOpen()) {
        flushParagraphBuffer();
        // 添加图片标签
        mTextEncoder.addImageTag(id, tag);
    } else {
        beginParagraph();
        // 添加图片标签控制位
        mTextEncoder.addControlTag(TextKind::IMAGE, true);
        mTextEncoder.addImageTag(id, tag);
        mTextEncoder.addControlTag(TextKind::IMAGE, false);
        endParagraph();
    }
}

/**
 * 图片资源标签
 *
 * 1. 资源类型：占用 1 字节。 image
 * 2. 边缘对齐：占用 1 字节。
 * 3. 资源 id：占 2 字节
 * 4. 资源路径长：占 2 字节。
 * 5. 资源路径：未知字节
 * @param tag
 * @return
 */
uint16_t BookEncoder::addImageResource(const ImageTag &tag) {
    // TODO:代码有问题先这样写。(应该存储 path 和 id 的映射关系)
    uint16_t resId = generateResourceId();

    size_t resourceLen = 4;
    // 长度 + 文本长
    resourceLen += 2 + UnicodeUtil::utf8Length(tag.path) * 2;

    char *resPtr = mResAllocator->allocate(resourceLen);

    *resPtr++ = (char) TextResType::IMAGE;
    *resPtr++ = 0;
    resPtr = TextBufferAllocator::writeUInt16(resPtr, resId);

    // 先转换成 utf-16
    UnicodeUtil::Ucs2String ucs2Path;
    UnicodeUtil::utf8ToUcs2(ucs2Path, tag.path);

    // 使用 allocator 进行存储文本
    TextBufferAllocator::writeString(resPtr, ucs2Path);

    return resId;
}

std::string
BookEncoder::addFontTag(const std::string &family, std::shared_ptr<FontEntry> fontEntry) {
    // TODO:到底名字要不要叫 resource 呢？先不想了
}

void BookEncoder::insertPseudoEndOfSectionParagraph() {
    insertEndParagraph(TextParagraph::PSEUDO_END_OF_SECTION_PARAGRAPH);
}

void BookEncoder::addExtResource() {
    // TODO：扩展资源信息，暂时用不到。
}

void BookEncoder::addBookExtResource() {
    // TODO: 扩展书籍资源，暂时用不到。
}