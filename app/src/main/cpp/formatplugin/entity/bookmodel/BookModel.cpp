// author : newbiechen
// date : 2019-09-20 14:45
// description : 
//

#include <util/AndroidUtil.h>
#include "BookModel.h"

BookModel::BookModel(const std::shared_ptr<Book> book, jobject jBookModel, const std::string &bookCacheDir)
        : mBook(book), cacheDir(bookCacheDir) {
    // 创建一个全局引用
    mJavaModel = AndroidUtil::getEnv()->NewGlobalRef(jBookModel);
    // TODO:创建 textModel
    mTextModel = new TextModel();
}

BookModel::~BookModel() {
    AndroidUtil::getEnv()->DeleteGlobalRef(mJavaModel);
}

const std::shared_ptr<Book> BookModel::getBook() const {
    return mBook;
}

std::shared_ptr<TextModel> BookModel::getTextModel() const {
    return mTextModel;
}