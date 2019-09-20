// author : newbiechen
// date : 2019-09-20 14:21
// description : 
//

#ifndef NBREADER_BOOK_H
#define NBREADER_BOOK_H

#include <string>
#include <jni.h>

/**
 * TODO:
 *
 * 1. 书籍可能有多位作者，方便起见如果出现多位作者，使用 / 分割并写成一行。 ==> 非重要内容
 * 2. C++ 的 File 对象不是很清楚，是否有必要自己创建一个 File 类
 */
class Book {
public:

    static std::shared_ptr<Book> createByJavaBook(JNIEnv *env, jobject jBook);

    Book();

    ~Book();

    const std::string &getTitle() const;

    const std::string &getLanguage() const;

    const std::string &getEncoding() const;

    int getBookId() const;

    const std::string &getAuthor() const;

    void setTitle(const std::string &title);

    void setLanguage(const std::string &language);

    void setEncoding(const std::string &encoding);

    void setAuthor(std::string *author);

private:
    int mBookId;
    std::string mTitle;
    std::string mLanguage;
    std::string mEncoding;

    Book(const Book &);

    const Book &operator=(const Book &);
};


#endif //NBREADER_BOOK_H
