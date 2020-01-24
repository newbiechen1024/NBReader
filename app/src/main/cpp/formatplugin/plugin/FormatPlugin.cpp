//
// Created by 陈广祥 on 2019-09-18.
//

#include <tools/language/LangDetector.h>
#include "FormatPlugin.h"
#include "PluginManager.h"
#include <util/Logger.h>

std::string FormatPlugin::CHAPTER_PROLOGUE_TITLE;

FormatPlugin::FormatPlugin() : mPath(""), mTitle(""), mEncoding(""),
                               mLanguage(""), mCachePath(""), mFilePtr(nullptr),
                               mChapterPattern("") {

    CHAPTER_PROLOGUE_TITLE = "";
}

FormatPlugin::~FormatPlugin() {
    if (mFilePtr != nullptr) {
        delete (mFilePtr);
    }
}

void FormatPlugin::setConfigure(
        const std::string &cachePath,
        const std::string &chapterPattern,
        const std::string &chapterPrologueTitle) {

    // TODO:是否应该有一个开启缓存的选项
    mCachePath = cachePath;
    // TODO：如果 title 存在，则直接从缓冲区读取数据
    mChapterPattern = chapterPattern;

    CHAPTER_PROLOGUE_TITLE = chapterPrologueTitle;
}

void FormatPlugin::setBookResource(Book &book) {

    // TODO:是否需要将 Book 的信息存储到缓冲区中？

    // TODO:如果 cachePath 存在，则直接从缓冲区读取数据

    // TODO: configure 与 book resource 读取三级缓存的问题，需要考虑一下，不过现在不实现。

    // TODO：三级缓存信息存在过期可能，需要判断处理。
}

void FormatPlugin::setBookResource(const std::string &bookPath) {
    // 如果传入的文本相同则不处理
    if (mPath == bookPath) {
        return;
    }

    mPath = bookPath;

    // 文件处理

    if (mFilePtr != nullptr) {
        delete (mFilePtr);
    }

    mFilePtr = new File(mPath);

    // 如果文件不存在则抛出异常
    if (!mFilePtr->exists()) {
        delete (mFilePtr);
        mFilePtr = nullptr;

        // TODO:文件不存在，抛出异常，暂时用崩溃代替
        fprintf(stderr,
                "setBookResource.\n");
        exit(-1);
    }

    // 将 path 的尾缀名设置为当前的 title
    mTitle = mFilePtr->getName();

    // TODO:从二级缓存中获取数据。
}

bool FormatPlugin::readEncoding(std::string &outEncoding) {
    // TODO:如果未设置 resource，则直接崩溃

    // 如果存在缓存
    if (!mEncoding.empty()) {
        outEncoding = mEncoding;
        return true;
    }

    bool result = readEncodingInternal(outEncoding);

    if (result) {
        // 设置一级缓冲
        mEncoding = outEncoding;
        // TODO:将数据写到本地缓冲区中
    }
    return result;
}

bool FormatPlugin::readLanguage(std::string &outLanguage) {
    // TODO:如果未设置 resource，则直接崩溃

    // 如果存在缓存
    if (!mLanguage.empty()) {
        outLanguage = mLanguage;
        return true;
    }

    bool result = readLanguageInternal(outLanguage);

    if (result) {
        mLanguage = outLanguage;

        // TODO:将数据写到本地缓冲区中
    }
    return result;
}

bool FormatPlugin::readChapters(std::vector<TextChapter> &chapterList) {
    if (!mChapterList.empty()) {
        chapterList = mChapterList;
        return true;
    }

    if (mChapterPattern.empty()) {
        return false;
    }

    bool result = readChaptersInternal(mChapterPattern, chapterList);

    if (result) {
        mChapterList.clear();
        mChapterList = chapterList;
        // TODO:保存数据到本地
    }

    return result;
}

bool FormatPlugin::readChapterContent(TextChapter &txtChapter, char **outBuffer, size_t *outSize) {
    // TODO：查找本地数据是否存在该章节的缓冲信息，如果存在则从本地数据读取

    bool result = readChapterContentInternal(txtChapter, outBuffer, outSize);

    if (result) {
        // TODO:存储数据到本地
    }

    return result;
}

bool FormatPlugin::detectEncoding(std::string &outEncoding) {
    // 获取输入流
    std::shared_ptr<InputStream> is = mFilePtr->getInputStream();

    // 检测文本的 encoding 和语言
    if (!is->open()) {
        return false;
    }

    // 选取 1 KB 的数据
    const int BUFF_SIZE = 1024;

    char *buffer = new char[BUFF_SIZE]();

    // 读取数据
    const std::size_t size = is->read(buffer, BUFF_SIZE);

    // 关闭文本流
    is->close();

    // 探测文本的编码
    std::string encoding = LangDetector::findEncoding(buffer, size);

    // TODO:FBReader 上加的代码，暂时不知道其作用。
    if (encoding == Charset::ASCII || encoding == "iso-8859-1") {
        encoding = "windows-1252";
    }
    // 赋值操作
    outEncoding = encoding;
    // 删除缓冲区
    delete[] buffer;
    // 返回成功
    return true;
}

bool FormatPlugin::detectLanguage(std::string &outLanguage) {
    // 获取输入流
    std::shared_ptr<InputStream> is = mFilePtr->getInputStream();

    if (!is->open()) {
        return false;
    }

    // 选取 1 KB 的数据
    // TODO：FBReader 是 65535 我嫌效率慢，最大获取 30 K 的数据。
    const int BUFF_SIZE = 30 * 1024;
    char *buffer = new char[BUFF_SIZE]();
    // 读取数据
    const std::size_t size = is->read(buffer, BUFF_SIZE);
    // 关闭文本流
    is->close();
    // 创建探测器，探测语言
    std::shared_ptr<LangDetector::LangInfo> langInfo;

    if (!mEncoding.empty()) {
        langInfo = LangDetector().findLanguageWithEncoding(mEncoding,
                                                           buffer, size);
    } else {
        langInfo = LangDetector().findLanguage(buffer, size);
    }

    delete[] buffer;

    if (langInfo != nullptr) {
        // 赋值操作
        outLanguage = langInfo->lang;
        return true;
    }
    // 返回失败
    return false;
}