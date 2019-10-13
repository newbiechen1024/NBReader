// author : newbiechen
// date : 2019-09-27 17:41
// description : 为 TextModel 文本添加对应的类型标记
//

#ifndef NBREADER_BOOKREADER_H
#define NBREADER_BOOKREADER_H


#include "BookModel.h"
#include "NBTextStyle.h"
#include "TOCTree.h"
#include <stack>

// 这是一次性使用的类，所以 BookModel 是持有传入的 BookModel 的引用。
class BookReader {
public:
    BookReader(BookModel &model);

    ~BookReader() {
    }

    // 文本样式标记入栈
    void pushTextStyle(NBTextStyle style);

    // 文本样式标记出栈
    bool popTextStyle();

    // 开始处理段落，传入参数指定段落段落
    void beginParagraph(TextParagraph::Type type = TextParagraph::TEXT_PARAGRAPH);

    // 结束段落处理
    void endParagraph();

    // 添加文本数据
    void addText(const std::string &text);

    // 添加标题文本
    void addTitleText(const std::string &text);

    // 插入 section paragraph 结束标记
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

private:
    void insertEndParagraph(TextParagraph::Type type);

    // 将段落缓冲输出到 textModel 中
    void flushParagraphBuffer();

private:
    BookModel &mBookModel;
    std::shared_ptr<TextModel> mTextModel;
    std::vector<NBTextStyle> mTextStyleList;
    std::vector<std::string> mParagraphBufferList;
    std::stack<std::shared_ptr<TOCTree> > mTOCTreeStack;
    bool isParagraphOpen;
    bool isTitleParagraphOpen;
};


#endif //NBREADER_BOOKREADER_H
