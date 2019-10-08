// author : newbiechen
// date : 2019-10-06 18:59
// description : 
//

#include "TxtReader.h"

TxtReader::TxtReader(BookModel &model, const PlainTextFormat &format, Charset charset) : EncodingTextReader(charset),
                                                                                         mBookReader(model),
                                                                                         mFormat(format) {
}

void TxtReader::startDocument() {
    // 处理操作
}

void TxtReader::readDocument() {

}

void TxtReader::endDocument() {

}

bool TxtReader::createNewLine() {

}

void TxtReader::endParagraphInternal() {

}