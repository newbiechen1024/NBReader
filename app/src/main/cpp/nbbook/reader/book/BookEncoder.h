// author : newbiechen
// date : 2019-12-30 16:17
// description : 书籍编码器
//

#ifndef NBREADER_BOOKENCODER_H
#define NBREADER_BOOKENCODER_H

#include "../text/tag/TextKind.h"
#include "../text/entity/TextParagraph.h"
#include "../text/TextEncoder.h"
#include <string>
#include <vector>
#include <stack>

class BookEncoder {
public:
    BookEncoder();

    ~BookEncoder() {
    }

    void open();

    size_t close(char **outBuffer);

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

private:
    void insertEndParagraph(TextParagraph::Type type);

    // 将段落缓冲输出到 textModel 中
    void flushParagraphBuffer();

private:
    // 文本编码器
    TextEncoder mTextEncoder;
    // 文本样式栈
    std::vector<TextKind> mTextKindStack;
    // 段落文本列表
    std::vector<std::string> mParagraphTextList;
    // 是否已经存在打开的段落
    bool isParagraphOpen;
    bool isTitleParagraphOpen;
    // 是否区域包含纯文本内容
    bool isSectionContainsRegularContents;
    // 当前是否正在输入标题
    bool isEnterTitle;
};

#endif //NBREADER_BOOKENCODER_H
