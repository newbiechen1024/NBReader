//
// Created by 陈广祥 on 2019-09-18.
//

#ifndef NBREADER_FORMATPLUGIN_H
#define NBREADER_FORMATPLUGIN_H


#include <string>
#include "FormatType.h"
#include "../reader/book/Book.h"
#include "../reader/text/entity/TextChapter.h"
#include "../filesystem/File.h"

class FormatPlugin {
public:
    FormatPlugin();

    virtual ~FormatPlugin();

    /**
     * TODO：暂时配置项只有缓存路径
     * @param cachePath：缓存路径
     */
    void setConfigure(const std::string &cachePath,
                      const std::string &chapterPattern,
                      const std::string &chapterPrologueTitle);

    /**
     * 传入自定义的书籍信息，之后的 read 操作都会直接从 book 中获取数据。
     * @param book: 传入之前以及处理好的资源
     */
    void setBookResource(Book &book);

    /**
     * 传入带解析的书籍路径。
     * @param bookPath：传入书籍路径
     */
    void setBookResource(const std::string &bookPath);

    const std::string &getPath() const {
        return mPath;
    }

    // 读取编码
    bool readEncoding(std::string &outEncoding);

    // 读取语言
    bool readLanguage(std::string &outLanguage);

    // 读取章节
    bool readChapters(std::vector<TextChapter> &chapterList);

    /**
     * 读取章节内容
     * @param txtChapter：需要读取的章节信息
     * @param outBuffer：输出缓冲区的指针，需要外部自己释放。
     * @param outSize：输出缓冲区的大小
     * @return
     */
    bool readChapterContent(TextChapter &txtChapter, char **outBuffer, size_t *outSize);

    virtual bool readEncodingInternal(std::string &outEncoding) = 0;

    virtual bool readLanguageInternal(std::string &outLanguage) = 0;

    virtual bool
    readChaptersInternal(std::string &chapterPattern, std::vector<TextChapter> &chapterList) = 0;

    virtual bool
    readChapterContentInternal(TextChapter &txtChapter, char **outBuffer, size_t *outSize) = 0;

    // todo:读取源信息，暂不实现
    // virtual bool readMetaInfo() = 0;
protected:
    // 探测语言
    bool detectLanguage(std::string &outLanguage);

    // 探测编码
    bool detectEncoding(std::string &outEncoding);

    // 提供子类写入缓冲的方法
    File &getBookFile() {
        return *mFilePtr;
    }

private:
    // 读取文本缓存
    void readTextCache();

public:
    // 序章的标题名
    static std::string CHAPTER_PROLOGUE_TITLE;

protected:
    // TODO:通过 get 拿更好吧，protected 容易出错

    // 书籍路径
    std::string mPath;
    // 书籍名
    std::string mTitle;
    // 书籍编码
    std::string mEncoding;
    // 书籍语言
    std::string mLanguage;
    // 章节列表
    std::vector<TextChapter> mChapterList;
    // TODO:作者信息

    // 文件指针
    File *mFilePtr;

private:
    // 缓存存储路径
    std::string mCachePath;
    // 章节匹配正则
    std::string mChapterPattern;
};

#endif //NBREADER_FORMATPLUGIN_H
