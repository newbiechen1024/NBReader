// author : newbiechen
// date : 2019-12-30 16:17
// description : 
//

#include "BookEncoder.h"

BookEncoder::BookEncoder() {
    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;
    isEnterTitle = false;
}

void BookEncoder::open() {
    mTextEncoder.open();
}

size_t BookEncoder::close(char **outBuffer) {
    size_t outSize = mTextEncoder.close(outBuffer);

    isParagraphOpen = false;
    isSectionContainsRegularContents = false;
    isTitleParagraphOpen = false;

    mParagraphTextList.clear();
    mTextStyleStack.clear();

    return outSize;
}

void BookEncoder::beginParagraph(TextParagraph::Type type) {
    // 结束当前段落
    endParagraph();

    // 创建段落
    mTextEncoder.createParagraph(type);

    // 将所有的 style 作为标记添加到新创建的段落中
    for (TextStyleType &textStyle : mTextStyleStack) {
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
 * @param style ：文本样式类型
 */
void BookEncoder::pushTextStyle(TextStyleType style) {
    mTextStyleStack.push_back(style);
}

/**
 * 删除文本样式
 * @return
 */
bool BookEncoder::popTextStyle() {
    if (!mTextStyleStack.empty()) {
        mTextStyleStack.pop_back();
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
