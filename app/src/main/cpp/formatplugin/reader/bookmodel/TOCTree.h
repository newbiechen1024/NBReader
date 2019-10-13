// author : newbiechen
// date : 2019-10-13 18:29
// description : 目录树
//

#ifndef NBREADER_TOCTREE_H
#define NBREADER_TOCTREE_H

#include <string>
#include <memory>
#include <vector>

class TOCTree {

public:
    TOCTree(int paragraphIndex = -1) : mParagraphIndex(paragraphIndex) {
    }

    ~TOCTree() {
    }

    void addText(const std::string &text) {
        mContent += text;
    }

    void addChild(std::shared_ptr<TOCTree> child) {
        mChildren.push_back(child);
    }

    const std::string &getText() const {
        return mContent;
    }

    int getParagraphIndex() const {
        return mParagraphIndex;
    }

    const std::vector<std::shared_ptr<TOCTree> > &getChildren() const {
        return mChildren;
    }

private:
    // 目录标题
    std::string mContent;
    // 目录文本对应书籍中的段落索引
    const int mParagraphIndex;
    // 子目录树
    std::vector<std::shared_ptr<TOCTree> > mChildren;
};

#endif //NBREADER_TOCTREE_H
