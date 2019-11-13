// author : newbiechen
// date : 2019-09-20 14:45
// description : 
//

#include <util/AndroidUtil.h>
#include "BookModel.h"

BookModel::BookModel(const std::shared_ptr<Book> book, jobject jBookModel,
                     const std::string &bookCacheDir)
        : mBook(book), cacheDir(bookCacheDir) {
    // 创建一个全局引用
    mJavaModel = AndroidUtil::getEnv()->NewGlobalRef(jBookModel);
    mTextModel = std::make_shared<TextPlainModel>("", book->getLanguage(), 131072, cacheDir,
                                                  "nb", mFontManager);
    mTOCTree = std::make_shared<TOCTree>();
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

std::shared_ptr<TOCTree> BookModel::getTOCTree() const {
    return mTOCTree;
}

bool BookModel::flush() {
    // 强制缓冲
    mTextModel->flush();
    // 判断是否强制缓冲失败
    if (mTextModel->allocator().isFailed()) {
        return false;
    }
    return true;
}