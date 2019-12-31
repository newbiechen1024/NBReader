//
// Created by 陈广祥 on 2019-09-19.
//

#include <filesystem/io/FileInputStream.h>
#include <util/Logger.h>
#include <sstream>
#include <reader/text/entity/TextChapter.h>
#include "TxtPlugin.h"
#include "PlainTextFormat.h"
#include "TxtReader.h"
#include "TxtChapterDetector.h"


TxtPlugin::TxtPlugin() {
}

TxtPlugin::~TxtPlugin() {
    if (mTxtReaderPtr != nullptr) {
        delete mTxtReaderPtr;
        mTxtReaderPtr = nullptr;
    }
}

bool TxtPlugin::readEncodingInternal(std::string &outEncoding) {
    return detectEncoding(outEncoding);
}

bool TxtPlugin::readLanguageInternal(std::string &outLanguage) {
    return detectLanguage(outLanguage);
}

bool
TxtPlugin::readChaptersInternal(std::string &chapterPattern,
                                std::vector<TextChapter> &chapterList) {
    //
    // std::string pattern = "^(.{0,8})(\xe7\xac\xac)([0-9\xe9\x9b\xb6\xe4\xb8\x80\xe4\xba\x8c\xe4\xb8\xa4\xe4\xb8\x89\xe5\x9b\x9b\xe4\xba\x94\xe5\x85\xad\xe4\xb8\x83\xe5\x85\xab\xe4\xb9\x9d\xe5\x8d\x81\xe7\x99\xbe\xe5\x8d\x83\xe4\xb8\x87\xe5\xa3\xb9\xe8\xb4\xb0\xe5\x8f\x81\xe8\x82\x86\xe4\xbc\x8d\xe9\x99\x86\xe6\x9f\x92\xe6\x8d\x8c\xe7\x8e\x96\xe6\x8b\xbe\xe4\xbd\xb0\xe4\xbb\x9f]{1,10})([\xe7\xab\xa0\xe8\x8a\x82\xe5\x9b\x9e\xe9\x9b\x86\xe5\x8d\xb7])(.{0,30})$";

    std::string encoding;
    bool result = readEncoding(encoding);

    if (result) {
        auto inputStream = mFilePtr->getInputStream();
        // 创建章节探测器，进行章节探测。
        TxtChapterDetector(chapterPattern).detector(inputStream, encoding, chapterList);
    }

    return result;
}

bool
TxtPlugin::readChapterContentInternal(TextChapter &txtChapter, char **outBuffer, size_t *outSize) {

    // TODO:对于文本独有的数据信息，如何进行缓存？就是解析 format 的情况，看下效率吧，如果效率特别高，不缓存也没问题(暂时不考虑)

    File chapterFile(txtChapter.url);
    // 如果参数信息未初始化
    if (!mFormat.hasInitialized()) {
        // 调用探测器进行探测
        PlainTextDetector detector;
        detector.detect(chapterFile, mFormat);
    }

    // 如果无法探测到信息，直接 return
    if (!mFormat.hasInitialized()) {
        return false;
    }

    std::string bookEncoding;

    readEncoding(bookEncoding);

    // 创建文本阅读器
    if (mTxtReaderPtr == nullptr) {
        mTxtReaderPtr = new TxtReader(mFormat, bookEncoding);
    }

    // 通过阅读器解析章节内容
    size_t result = mTxtReaderPtr->readContent(txtChapter, outBuffer);

    // 是否解析失败
    if (result < 0) {
        return false;
    }

    (*outSize) = result;
    return false;
}
