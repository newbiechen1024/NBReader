// author : newbiechen
// date : 2019-09-27 17:41
// description : 
//

#include "BookReader.h"

BookReader::BookReader(BookModel &model) : mBookModel(model) {
    mTextModel = mBookModel.getTextModel();
}

void BookReader::beginParagraph(TextParagraph::Type type) {
    // 结束当前段落
    endParagraph();
    // 在 TextModel 中创建段落
    std::static_pointer_cast<TextPlainModel>(mTextModel)->createParagraph(type);
    // 初始化 textmodel 中的 paragraph

    // 将所有的 style 作为标记添加到新创建的段落中
    for (NBTextStyle &textStyle : mTextStyleList) {
        mTextModel->addControl(static_cast<TextStyle>(textStyle), true);
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
    mTextModel->addTexts(mParagraphBufferList);
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

void BookReader::pushTextStyle(NBTextStyle style) {
    mTextStyleList.push_back(style);
}

bool BookReader::popTextStyle() {
    mTextStyleList.pop_back();
}

void BookReader::insertEndOfSectionParagraph() {

}