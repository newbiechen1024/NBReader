// author : newbiechen
// date : 2019-09-20 14:45
// description : 
//

#ifndef NBREADER_BOOKMODEL_H
#define NBREADER_BOOKMODEL_H

#include <string>
#include <jni.h>
#include <reader/book/Book.h>
#include <reader/textmodel/TextModel.h>
#include <memory>
#include "TOCTree.h"

class BookModel {
public:

    BookModel(const std::shared_ptr<Book> book, jobject jBookModel, const std::string &cacheDir);

    ~BookModel();

    const std::shared_ptr<Book> getBook() const;

    std::shared_ptr<TextModel> getTextModel() const;

    // 获取目录数
    std::shared_ptr<TOCTree> getTOCTree() const;

public:
    const std::string cacheDir;

private:
    const std::shared_ptr<Book> mBook;
    std::shared_ptr<TextModel> mTextModel;
    std::shared_ptr<TOCTree> mTOCTree;

    jobject mJavaModel;
};


#endif //NBREADER_BOOKMODEL_H
