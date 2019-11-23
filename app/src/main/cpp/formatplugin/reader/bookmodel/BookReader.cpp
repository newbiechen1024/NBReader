// author : newbiechen
// date : 2019-09-27 17:41
// description : 
//

#include <reader/textmodel/tag/NBTagStyle.h>
#include "BookReader.h"

BookReader::BookReader(BookModel &model) : mBookModel(model) {
    mTextModel = mBookModel.getTextModel();
    isSectionContainsRegularContents = false;
}

void BookReader::beginParagraph(TextParagraph::Type type) {
    // 结束当前段落
    endParagraph();
    // 在 TextModel 中创建段落
    std::static_pointer_cast<TextPlainModel>(mTextModel)->createParagraph(type);
    // 初始化 textmodel 中的 paragraph

    // 将所有的 style 作为标记添加到新创建的段落中
    for (NBTagStyle &textStyle : mTagStyleStack) {
        mTextModel->addControlTag(static_cast<NBTagStyle>(textStyle), true);
    }

    // 标记当前段落正在处理
    isParagraphOpen = true;
}

void BookReader::addText(const std::string &text) {
    if (text.empty() || !hasParagraphOpen()) {
        return;
    }

    // 添加段落文本到段落缓冲区
    mParagraphBufferList.push_back(text);
}

void BookReader::endParagraph() {
    // 如果段落未开启，则不需要结束
    if (!hasParagraphOpen()) {
        return;
    }
    // 输出段落缓冲到实际段落中
    flushParagraphBuffer();
    isParagraphOpen = false;
}

void BookReader::flushParagraphBuffer() {
    // 将当前获取到的文本添加到 TextModel 中
    mTextModel->addTextTag(mParagraphBufferList);
    mParagraphBufferList.clear();
}

void BookReader::beginTitleParagraph(int paragraphIndex) {
    // 获取父 TOCTree
    auto parentTree = mTOCTreeStack.empty() ? mBookModel.getTOCTree() : mTOCTreeStack.top();

    // 默认段落索引为当前段落数
    if (paragraphIndex == -1) {
        paragraphIndex = mTextModel->getParagraphCount();
    }

    // TODO: 如果 Tree 没有获取到标题，则用 ... 代替
    if (parentTree->getText().empty()) {
        parentTree->addText("...");
    }

    auto tocTree = std::make_shared<TOCTree>(paragraphIndex);
    parentTree->addChild(tocTree);
    // 将当前 tree 加入到待完成栈中
    mTOCTreeStack.push(tocTree);
    // 标记正在处理标题段落
    isTitleParagraphOpen = true;
}

void BookReader::addTitleText(const std::string &text) {
    if (!hasTitleParagraphOpen() || text.empty()) {
        return;
    }

    // 获取栈顶的 TOCTree 并添加文本信息
    mTOCTreeStack.top()->addText(text);
}

void BookReader::endTitleParagraph() {
    if (!hasTitleParagraphOpen() || mTOCTreeStack.empty()) {
        return;
    }

    auto tocTree = mTOCTreeStack.top();
    if (tocTree->getText().empty()) {
        tocTree->addText("...");
    }
    // 将 Tree 出栈
    mTOCTreeStack.pop();

    // 标记标题段落结束
    isTitleParagraphOpen = false;
}

/**
 * 添加文本样式
 * @param style ：文本样式类型
 */
void BookReader::pushTextStyle(NBTagStyle style) {
    mTagStyleStack.push_back(style);
}

/**
 * 删除文本样式
 * @return
 */
bool BookReader::popTextStyle() {
    if (!mTagStyleStack.empty()) {
        mTagStyleStack.pop_back();
        return true;
    }
    return false;
}

void BookReader::insertEndOfSectionParagraph() {
    insertEndParagraph(TextParagraph::END_OF_SECTION_PARAGRAPH);
}

// 插入结束段落，保证 section 与文本不结合在一起
void BookReader::insertEndParagraph(TextParagraph::Type type) {
    // 如果片段是标准的内容
    if (mTextModel != 0 && isSectionContainsRegularContents) {
        std::size_t size = mTextModel->getParagraphCount();

        // FBReader 这么写的，暂时先不改。
        if (size > 0 && ((*mTextModel)[size - 1])->type != type) {
            endParagraph();
            // 通知  TextModel 创建新段落
            ((TextPlainModel &) *mTextModel).createParagraph(type);
            isSectionContainsRegularContents = false;
        }
    }
}