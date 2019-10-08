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

class BookModel {
public:
    const std::string cacheDir;

    BookModel(const std::shared_ptr<Book> book, jobject jBookModel, const std::string &cacheDir);

    ~BookModel();

    const std::shared_ptr<Book> getBook() const;

    std::shared_ptr<TextModel> getTextModel() const;


private:
    const std::shared_ptr<Book> mBook;
    std::shared_ptr<TextModel> mTextModel;
    jobject mJavaModel;
};


#endif //NBREADER_BOOKMODEL_H
