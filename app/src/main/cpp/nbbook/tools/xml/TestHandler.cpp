// author : newbiechen
// date : 2020-02-16 19:01
// description : 
//

#include "TestHandler.h"
#include "../../../../../../../../../../Library/Android/sdk/ndk-bundle/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/c++/v1/string"
#include "../../util/Logger.h"

static const std::string TAG = "TestHandler";

void TestHandler::startDocument() {
    Logger::i(TAG, "startDocument");
}

void TestHandler::endDocument() {
    Logger::i(TAG, "endDocument");
}

void
TestHandler::startElement(std::string &localName, std::string &fullName, Attributes &attributes) {
    Logger::i(TAG, "startElement  local:" + localName + "  fullName:" + fullName);
}

void TestHandler::characterData(std::string &data) {
    Logger::i(TAG, "characterData   data:" + data);
}

void TestHandler::endElement(std::string &localName, std::string &fullName) {
    Logger::i(TAG, "endElement  local:" + localName + "  fullName:" + fullName);
}

void TestHandler::error(std::string &err) {
    Logger::i(TAG, "error:" + err);
}
