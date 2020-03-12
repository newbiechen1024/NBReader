#include <cstdlib>

#include "NcxReader.h"
#include "../../util/MiscUtil.h"
#include "../../tools/xml/SAXParserFactory.h"
#include "../../util/Logger.h"

static const std::string TAG_NAVMAP = "navMap";
static const std::string TAG_NAVPOINT = "navPoint";
static const std::string TAG_NAVLABEL = "navLabel";
static const std::string TAG_CONTENT = "content";
static const std::string TAG_TEXT = "text";


NcxReader::NcxReader() : myReadState(READ_NONE),
                         myPlayIndex(-65535) {
}

void NcxReader::readFile(const File &ncxFile) {
    auto parser = SAXParserFactory::getParser();

    mNcxDirPath = MiscUtil::htmlDirectoryPrefix(ncxFile.getPath());
    parser->parse(ncxFile, *this);
}

void
NcxReader::startElement(std::string &localName, std::string &fullName, Attributes &attributes) {
    std::string tag = localName;
    switch (myReadState) {
        case READ_NONE:
            if (TAG_NAVMAP == tag) {
                myReadState = READ_MAP;
            }
            break;
        case READ_MAP:
            if (TAG_NAVPOINT == tag) {
                std::string order = attributes.getValue("playOrder");
                myPointStack.push_back(
                        NavPoint(!order.empty() ? std::atoi(order.c_str()) : myPlayIndex++,
                                 myPointStack.size()));
                myReadState = READ_POINT;
            }
            break;
        case READ_POINT:
            if (TAG_NAVPOINT == tag) {
                std::string order = attributes.getValue("playOrder");
                myPointStack.push_back(
                        NavPoint(!order.empty() ? std::atoi(order.c_str()) : myPlayIndex++,
                                 myPointStack.size()));
            } else if (TAG_NAVLABEL == tag) {
                myReadState = READ_LABEL;
            } else if (TAG_CONTENT == tag) {
                std::string src = attributes.getValue("src");
                if (!src.empty()) {
                    myPointStack.back().ContentHRef = MiscUtil::decodeHtmlURL(src);
                }
            }
            break;
        case READ_LABEL:
            if (TAG_TEXT == tag) {
                myReadState = READ_TEXT;
            }
            break;
        case READ_TEXT:
            break;
    }
}

void NcxReader::characterData(std::string &data) {
    if (myReadState == READ_TEXT) {
        myPointStack.back().Text.append(data);
    }
}

void NcxReader::endElement(std::string &localName, std::string &fullName) {
    std::string tag = localName;

    switch (myReadState) {
        case READ_NONE:
            break;
        case READ_MAP:
            if (TAG_NAVMAP == tag) {
                myReadState = READ_NONE;
            }
            break;
        case READ_POINT:
            if (TAG_NAVPOINT == tag) {
                if (myPointStack.back().Text.empty()) {
                    myPointStack.back().Text = "...";
                }
                myNavigationMap[myPointStack.back().Order] = myPointStack.back();
                myPointStack.pop_back();
                myReadState = myPointStack.empty() ? READ_MAP : READ_POINT;
            }
        case READ_LABEL:
            if (TAG_NAVLABEL == tag) {
                myReadState = READ_POINT;
            }
            break;
        case READ_TEXT:
            if (TAG_TEXT == tag) {
                myReadState = READ_LABEL;
            }
            break;
    }
}

const std::map<int, NcxReader::NavPoint> &NcxReader::navigationMap() const {
    return myNavigationMap;
}

NcxReader::NavPoint::NavPoint() {
}

NcxReader::NavPoint::NavPoint(int order, std::size_t level) : Order(order), Level(level) {
}
