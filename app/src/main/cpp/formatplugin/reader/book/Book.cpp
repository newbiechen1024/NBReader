// author : newbiechen
// date : 2019-09-20 14:21
// description : 
//

#include <util/JNIEnvelope.h>
#include <util/AndroidUtil.h>
#include "Book.h"

Book::Book(const File &file) : mFile(file) {
}

Book::~Book() {
}

std::shared_ptr<Book> Book::createBook(const File &file,
                                       const std::string &encoding, const std::string &language,
                                       const std::string &title) {
    std::shared_ptr<Book> book = std::make_shared<Book>(file);
    book->setTitle(title);
    book->setEncoding(encoding);
    book->setLanguage(language);
    return book;
}

std::shared_ptr<Book> Book::createByJavaBook(jobject jBook) {
    using namespace std;
    // 从 Book 中获取数据
    string path = AndroidUtil::Method_Book_getUrl->callForCppString(jBook);
    string title = AndroidUtil::Method_Book_getTitle->callForCppString(jBook);
    string encoding = AndroidUtil::Method_Book_getEncoding->callForCppString(jBook);
    string language = AndroidUtil::Method_Book_getLang->callForCppString(jBook);
    // 创建 Book
    return createBook(File(path), encoding, language, title);
}

void Book::setTitle(const std::string &title) {
    mTitle = title;
}

void Book::setLanguage(const std::string &language) {
    // TODO: FBReader 有一个 language 集合的列表，传入的 lang 需要从列表中匹配。 ==> 暂时先不处理这一部分逻辑。
    mLanguage = language;
}

void Book::setEncoding(const std::string &encoding) {
    mEncoding = encoding;
}

void Book::setAuthor(const std::string &author) {
    mAuthor = author;
}

const std::string &Book::getTitle() const {
    return mTitle;
}

const std::string &Book::getEncoding() const {
    return mEncoding;
}

const std::string &Book::getLanguage() const {
    return mLanguage;
}

const std::string &Book::getAuthor() const {
    return mAuthor;
}

const File &Book::getFile() const {
    return mFile;
}