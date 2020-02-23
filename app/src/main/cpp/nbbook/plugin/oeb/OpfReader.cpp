// author : newbiechen
// date : 2020-02-23 10:48
// description : 
//

#include "OpfReader.h"
#include "../../tools/xml/SAXParserFactory.h"
#include "../../util/UnicodeUtil.h"
#include "../../tools/constant/XMLNamespace.h"
#include "../../util/MiscUtil.h"
#include "../../util/Logger.h"

static const std::string MANIFEST = "manifest";
static const std::string SPINE = "spine";
static const std::string GUIDE = "guide";
static const std::string TOUR = "tour";
static const std::string SITE = "site";

static const std::string ITEM = "item";
static const std::string ITEMREF = "itemref";
static const std::string REFERENCE = "reference";

static const std::string COVER = "cover";
static const std::string COVER_IMAGE = "other.ms-coverimage-standard";

bool OpfReader::isOpfTag(const std::string &expected, const std::string &tag) const {
    return expected == tag ||
           isRightTag(XMLNamespace::OpenPackagingFormat, expected, tag);
}

bool OpfReader::isDcTag(const std::string &expected, const std::string &tag) const {
    return isRightTag(XMLNamespace::DublinCore, expected, tag) ||
           isRightTag(XMLNamespace::DublinCoreLegacy, expected, tag);
}

bool OpfReader::isMetadataTag(const std::string &tagName) {
    static const std::string METADATA = "metadata";
    static const std::string DC_METADATA = "dc-metadata";

    return isRightTag(XMLNamespace::OpenPackagingFormat, METADATA, tagName) ||
           DC_METADATA == tagName;
}

void OpfReader::readFile(File &file) {
    // 清空信息
    myIdToHref.clear();
    myHtmlFileNames.clear();
    myNCXTOCFileName.erase();
    myCoverFileName.erase();
    myCoverFileType.erase();
    myCoverMimeType.erase();
    myTourTOC.clear();
    myGuideTOC.clear();
    myState = READ_NONE;

    myOpfDirPath = MiscUtil::htmlDirectoryPrefix(file.getPath());

    auto parser = SAXParserFactory::getParser();
    parser->parse(file, *this);
}

void
OpfReader::startElement(std::string &localName, std::string &fullName, Attributes &attributes) {
    std::string tagString = UnicodeUtil::toLower(fullName);

    switch (myState) {
        case READ_NONE:
            if (isOpfTag(MANIFEST, tagString)) {
                myState = READ_MANIFEST;
            } else if (isOpfTag(SPINE, tagString)) {
                std::string toc = attributes.getValue("toc");
                if (!toc.empty()) {
                    myNCXTOCFileName = myIdToHref[toc];
                }
                myState = READ_SPINE;
            } else if (isOpfTag(GUIDE, tagString)) {
                myState = READ_GUIDE;
            } else if (isOpfTag(TOUR, tagString)) {
                myState = READ_TOUR;
            }
            break;
        case READ_MANIFEST:
            if (isOpfTag(ITEM, tagString)) {
                std::string href = attributes.getValue("href");
                if (!href.empty()) {
                    const std::string sHref = MiscUtil::decodeHtmlURL(href);
                    std::string id = attributes.getValue("id");
                    std::string mediaType = attributes.getValue("media-type");
                    if (!id.empty()) {
                        myIdToHref[id] = sHref;
                    }
                    if (!mediaType.empty()) {
                        myHrefToMediatype[sHref] = mediaType;
                    }
                }
            }
            break;
        case READ_SPINE:
            if (isOpfTag(ITEMREF, tagString)) {
                std::string id = attributes.getValue("idref");
                if (!id.empty()) {
                    const std::string &fileName = myIdToHref[id];
                    if (!fileName.empty()) {
                        myHtmlFileNames.push_back(fileName);
                    }
                }
            }
            break;
        case READ_GUIDE:
            if (isOpfTag(REFERENCE, tagString)) {
                std::string type = attributes.getValue("type");
                std::string title = attributes.getValue("title");
                std::string href = attributes.getValue("href");
                if (!href.empty()) {
                    const std::string reference = MiscUtil::decodeHtmlURL(href);
                    if (!title.empty()) {
                        myGuideTOC.push_back(std::make_pair(std::string(title), reference));
                    }
                    if (!type.empty() && (COVER == type || COVER_IMAGE == type)) {
                        myCoverFileName = reference;
                        myCoverFileType = type;
                        const std::map<std::string, std::string>::const_iterator it =
                                myHrefToMediatype.find(reference);
                        myCoverMimeType =
                                it != myHrefToMediatype.end() ? it->second : std::string();
                    }
                }
            }
            break;
        case READ_TOUR:
            if (isOpfTag(SITE, tagString)) {
                std::string title = attributes.getValue("title");
                std::string href = attributes.getValue("href");
                if ((!title.empty()) && (!href.empty())) {
                    myTourTOC.push_back(std::make_pair(title, MiscUtil::decodeHtmlURL(href)));
                }
            }
            break;
    }
}

void OpfReader::endElement(std::string &localName, std::string &fullName) {
    std::string tagString = UnicodeUtil::toLower(fullName);

    switch (myState) {
        case READ_MANIFEST:
            if (isOpfTag(MANIFEST, tagString)) {
                myState = READ_NONE;
            }
            break;
        case READ_SPINE:
            if (isOpfTag(SPINE, tagString)) {
                myState = READ_NONE;
            }
            break;
        case READ_GUIDE:
            if (isOpfTag(GUIDE, tagString)) {
                myState = READ_NONE;
            }
            break;
        case READ_TOUR:
            if (isOpfTag(TOUR, tagString)) {
                myState = READ_NONE;
            }
            break;
        case READ_NONE:
            break;
    }
}

void OpfReader::error(std::string &err) {
}
