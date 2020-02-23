/*
 * Copyright (C) 2004-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

#include <cctype>

#include "XHTMLReader.h"
#include "../css/StyleSheetParser.h"
#include "../../util/MiscUtil.h"
#include "../xml/SAXParserFactory.h"
#include "../../util/UnicodeUtil.h"
#include "../../reader/book/BookEncoder.h"
#include "../../util/StringUtil.h"
#include "../../util/Logger.h"
#include "../../filesystem/FileSystem.h"
#include "../../util/FilePathUtil.h"
#include "../constant/XMLNamespace.h"

static const std::string ANY = "*";
static const std::string EMPTY = "";

static const XHTMLTagList EMPTY_INFO_LIST = XHTMLTagList();

std::map<std::string, XHTMLTagAction *> XHTMLReader::ourTagActions;
std::map<std::shared_ptr<NsXMLFilter>, XHTMLTagAction *> XHTMLReader::ourNsTagActions;

/*** Filter:XHTMLSvgImageNamePredicate ***/

class XHTMLSvgImageFilter : public NsXMLFilter {

public:
    XHTMLSvgImageFilter();

    bool accept(const BaseHandler &handler, const char *patternName) const override;

private:
    bool myIsEnabled;

    friend class XHTMLTagSvgAction;
};

XHTMLSvgImageFilter::XHTMLSvgImageFilter() : NsXMLFilter(
        XMLNamespace::XLink, "href"), myIsEnabled(false) {
}

bool XHTMLSvgImageFilter::accept(const BaseHandler &handler, const char *name) const {
    return myIsEnabled && NsXMLFilter::accept(handler, name);
}

/***XHTMLTagAction****/

XHTMLTagAction::~XHTMLTagAction() {
}

BookEncoder &XHTMLTagAction::getBookEncoder(XHTMLReader &reader) {
    return reader.myModelReader;
}

const std::string &XHTMLTagAction::pathPrefix(XHTMLReader &reader) {
    return reader.myPathPrefix;
}

void XHTMLTagAction::beginParagraph(XHTMLReader &reader) {
    reader.beginParagraph();
}

void XHTMLTagAction::endParagraph(XHTMLReader &reader) {
    reader.endParagraph();
}


/*** XHTMLGlobalTagAction ***/
class XHTMLGlobalTagAction : public XHTMLTagAction {

private:
    bool isEnabled(XHTMLReadingState state);
};

bool XHTMLGlobalTagAction::isEnabled(XHTMLReadingState state) {
    return true;
}

/*** XHTMLTextModeTagAction ***/

class XHTMLTextModeTagAction : public XHTMLTagAction {

private:
    bool isEnabled(XHTMLReadingState state);
};

bool XHTMLTextModeTagAction::isEnabled(XHTMLReadingState state) {
    return state == XHTML_READ_BODY;
}

/*** XHTMLTagStyleAction ***/

class XHTMLTagStyleAction : public XHTMLGlobalTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagStyleAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    static const std::string TYPE = "text/css";

    std::string type = attributes.getValue("type");
    if (type.empty() || TYPE != type) {
        return;
    }

    if (reader.myReadState == XHTML_READ_NOTHING) {
        reader.myReadState = XHTML_READ_STYLE;
        reader.myTableParser = std::make_shared<StyleSheetTableParser>(reader.myPathPrefix,
                                                                       reader.myStyleSheetTable,
                                                                       reader.myFontMap,
                                                                       reader.myEncryptionMap);
        Logger::i("CSS", "parsing style tag content");
    }
}

void XHTMLTagStyleAction::doAtEnd(XHTMLReader &reader) {
    if (reader.myReadState == XHTML_READ_STYLE) {
        reader.myReadState = XHTML_READ_NOTHING;
        reader.myTableParser.reset();
    }
}


/*** XHTMLTagLinkAction ***/


class XHTMLTagLinkAction : public XHTMLGlobalTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagLinkAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    static const std::string REL = "stylesheet";
    std::string rel = attributes.getValue("rel");
    if (rel.empty() || REL != UnicodeUtil::toLower(rel)) {
        return;
    }

    static const std::string TYPE = "text/css";

    std::string type = attributes.getValue("type");
    if (type.empty() || TYPE != UnicodeUtil::toLower(type)) {
        return;
    }

    std::string href = attributes.getValue("href");
    if (href.empty()) {
        return;
    }

    std::string cssFilePath = reader.myPathPrefix + MiscUtil::decodeHtmlURL(href);
    Logger::i("CSS", "style file: " + cssFilePath);
    const File cssFile(cssFilePath);
    cssFilePath = cssFile.getPath();
    std::shared_ptr<StyleSheetParserWithCache> parser = reader.myFileParsers[cssFilePath];
    if (parser == nullptr) {
        parser = std::make_shared<StyleSheetParserWithCache>(cssFile,
                                                             MiscUtil::htmlDirectoryPrefix(
                                                                     cssFilePath),
                                                             nullptr,
                                                             reader.myEncryptionMap);
        reader.myFileParsers[cssFilePath] = parser;
        Logger::i("CSS", "creating stream");
        std::shared_ptr<InputStream> cssStream = cssFile.getInputStream(reader.myEncryptionMap);
        if (cssStream != nullptr) {
            Logger::i("CSS", "parsing file");
            parser->parseStream(cssStream);
        }
    }
    parser->applyToTables(reader.myStyleSheetTable, *reader.myFontMap);
}

void XHTMLTagLinkAction::doAtEnd(XHTMLReader &) {
}


/*** XHTMLTagParagraphAction ***/

class XHTMLTagParagraphAction : public XHTMLTextModeTagAction {

private:
    const TextKind myTextKind;

public:
    XHTMLTagParagraphAction(TextKind textKind = TextKind::NONE);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

XHTMLTagParagraphAction::XHTMLTagParagraphAction(TextKind textKind) : myTextKind(textKind) {
}

void XHTMLTagParagraphAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    if (!reader.myNewParagraphInProgress) {
        reader.pushTextKind(myTextKind);
        reader.beginParagraph();
        reader.myNewParagraphInProgress = true;
    }
}

void XHTMLTagParagraphAction::doAtEnd(XHTMLReader &reader) {
    reader.endParagraph();
}


/*** XHTMLTagBodyAction ***/

class XHTMLTagBodyAction : public XHTMLGlobalTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagBodyAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    ++reader.myBodyCounter;
    if (reader.myBodyCounter > 0) {
        reader.myReadState = XHTML_READ_BODY;
    }
}

void XHTMLTagBodyAction::doAtEnd(XHTMLReader &reader) {
    endParagraph(reader);
    --reader.myBodyCounter;
    if (reader.myBodyCounter <= 0) {
        reader.myReadState = XHTML_READ_NOTHING;
    }
}


/*** XHTMLTagSectionAction ***/

class XHTMLTagSectionAction : public XHTMLGlobalTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagSectionAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
}

void XHTMLTagSectionAction::doAtEnd(XHTMLReader &reader) {
    getBookEncoder(reader).insertEndOfSectionParagraph();
}


/*** XHTMLTagPseudoSectionAction ***/


class XHTMLTagPseudoSectionAction : public XHTMLGlobalTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagPseudoSectionAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
}

void XHTMLTagPseudoSectionAction::doAtEnd(XHTMLReader &reader) {
    getBookEncoder(reader).insertPseudoEndOfSectionParagraph();
}


/*** XHTMLTagVideoAction ***/

class XHTMLTagVideoAction : public XHTMLTagAction {

private:
    bool isEnabled(XHTMLReadingState state);

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

bool XHTMLTagVideoAction::isEnabled(XHTMLReadingState state) {
    return state == XHTML_READ_BODY || state == XHTML_READ_VIDEO;
}

void XHTMLTagVideoAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    if (reader.myReadState == XHTML_READ_BODY) {
        reader.myReadState = XHTML_READ_VIDEO;
        reader.myVideoEntry = std::make_shared<VideoTag>();
    }
}

void XHTMLTagVideoAction::doAtEnd(XHTMLReader &reader) {
    if (reader.myReadState == XHTML_READ_VIDEO) {
        getBookEncoder(reader).addVideoTag(*reader.myVideoEntry);
        reader.myVideoEntry.reset();
        reader.myReadState = XHTML_READ_BODY;
    }
}

/*** XHTMLTagSourceAction ***/

class XHTMLTagSourceAction : public XHTMLTagAction {

private:
    bool isEnabled(XHTMLReadingState state);

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

bool XHTMLTagSourceAction::isEnabled(XHTMLReadingState state) {
    return state == XHTML_READ_VIDEO;
}

void XHTMLTagSourceAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    std::string mime = attributes.getValue("type");
    std::string href = attributes.getValue("src");
    if (!mime.empty() && !href.empty()) {
        reader.myVideoEntry->addSource(
                mime,
                File(pathPrefix(reader) + MiscUtil::decodeHtmlURL(href)).getPath()
        );
    }
}

void XHTMLTagSourceAction::doAtEnd(XHTMLReader &reader) {
}

/*** XHTMLTagImageAction ***/

class XHTMLTagImageAction : public XHTMLTextModeTagAction {

public:
    // TODO:支持自定义的过滤方式
    XHTMLTagImageAction(NsXMLFilter *predicate);

    XHTMLTagImageAction(const std::string &attributeName);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);

private:
    NsXMLFilter *mFilter;
    std::string mAttributeName;
};


XHTMLTagImageAction::XHTMLTagImageAction(NsXMLFilter *filter) {
    mFilter = filter;
}

XHTMLTagImageAction::XHTMLTagImageAction(const std::string &attributeName) {
    mAttributeName = attributeName;
}

void XHTMLTagImageAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    std::string fileName;

    if (mFilter != nullptr) {
        fileName = mFilter->pattern(reader, attributes);
    } else {
        fileName = attributes.getValue(mAttributeName);
    }

    if (fileName.empty()) {
        return;
    }

    const std::string fullFileName = pathPrefix(reader) + MiscUtil::decodeHtmlURL(fileName);
    File imageFile(fullFileName);
    if (!imageFile.exists()) {
        return;
    }

    const bool flagParagraphIsOpen = getBookEncoder(reader).hasParagraphOpen();
    if (flagParagraphIsOpen) {
        if (reader.myCurrentParagraphIsEmpty) {
            getBookEncoder(reader).addControlTag(TextKind::IMAGE, true);
        } else {
            endParagraph(reader);
        }
    }
    // TODO:添加图片标签，以及添加图片逻辑，这个需要考虑如何实现
    const std::string imageName = imageFile.getName();
    ImageTag imageTag(imageName, "", reader.myMarkNextImageAsCover, 0, 0, 0,
                      reader.myEncryptionMap);

    getBookEncoder(reader).addImageTag(imageTag);

/*    getBookEncoder(reader).addImageReference(imageName, 0, reader.myMarkNextImageAsCover);
    getBookEncoder(reader).addImage(imageName, new ZLFileImage(imageFile, EMPTY, 0, 0,
                                                               reader.myEncryptionMap->info(
                                                                       imageFile.path())));*/
    reader.myMarkNextImageAsCover = false;
    if (flagParagraphIsOpen && reader.myCurrentParagraphIsEmpty) {
        getBookEncoder(reader).addControlTag(TextKind::IMAGE, false);
        endParagraph(reader);
    }
}

void XHTMLTagImageAction::doAtEnd(XHTMLReader &) {
}

/*** XHTMLTagSvgAction ***/

class XHTMLTagSvgAction : public XHTMLTextModeTagAction {

public:
    XHTMLTagSvgAction(XHTMLSvgImageFilter *predicate);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);

private:
    XHTMLSvgImageFilter *myPredicate;
};

XHTMLTagSvgAction::XHTMLTagSvgAction(XHTMLSvgImageFilter *predicate) : myPredicate(
        predicate) {
}

void XHTMLTagSvgAction::doAtStart(XHTMLReader &, Attributes &attributes) {
    myPredicate->myIsEnabled = true;
}

void XHTMLTagSvgAction::doAtEnd(XHTMLReader &) {
    myPredicate->myIsEnabled = false;
}

/*** XHTMLTagListAction ***/

class XHTMLTagListAction : public XHTMLTextModeTagAction {

private:
    const int myStartIndex;

public:
    XHTMLTagListAction(int startIndex = -1);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};


XHTMLTagListAction::XHTMLTagListAction(int startIndex) : myStartIndex(startIndex) {
}

void XHTMLTagListAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    reader.myListNumStack.push(myStartIndex);
    beginParagraph(reader);
}

void XHTMLTagListAction::doAtEnd(XHTMLReader &reader) {
    endParagraph(reader);
    if (!reader.myListNumStack.empty()) {
        reader.myListNumStack.pop();
    }
}

/*** XHTMLTagItemAction ***/

class XHTMLTagItemAction : public XHTMLTextModeTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagItemAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    bool restart = true;
    if (reader.myTagDataStack.size() >= 2) {
        restart = reader.myTagDataStack[reader.myTagDataStack.size() - 2]->Children.size() > 1;
    }
    if (restart) {
        endParagraph(reader);
        beginParagraph(reader);
    }
    if (!reader.myListNumStack.empty()) {
        getBookEncoder(reader).addFixedHSpaceTag(3 * reader.myListNumStack.size());
        int &index = reader.myListNumStack.top();
        if (index == 0) {
            // TODO：暂时不知道这段文字的意思，UTF-8 输出也有问题
            static const std::string bullet = "\xE2\x80\xA2\xC0\xA0";
            getBookEncoder(reader).addText(bullet);
        } else {
            getBookEncoder(reader).addText(StringUtil::numberToString(index++) + ".");
        }
        getBookEncoder(reader).addFixedHSpaceTag(1);
    }
    reader.myNewParagraphInProgress = true;
}

void XHTMLTagItemAction::doAtEnd(XHTMLReader &reader) {
}


/*** XHTMLTagHyperlinkAction ***/

class XHTMLTagHyperlinkAction : public XHTMLTextModeTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);

private:
    std::stack<TextKind> myHyperlinkStack;
};

void XHTMLTagHyperlinkAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    std::string href = attributes.getValue("href");
    if (href.empty() && href[0] != '\0') {
        TextKind hyperlinkType = MiscUtil::referenceType(href);
        std::string link = MiscUtil::decodeHtmlURL(href);
        if (hyperlinkType == TextKind::INTERNAL_HYPERLINK) {
            static const std::string NOTEREF = "noteref";
            std::string epubType = attributes.getValue("epub:type");
            if (epubType.empty()) {
                // popular ePub mistake: ':' in attribute name coverted to ascii code
                std::string epubTypePredicate = "epubu0003atype";

                // 无视大小写处理
                StringUtil::asciiToLowerInline(epubTypePredicate);

                epubType = attributes.getValue(epubTypePredicate);
            }
            if (!epubType.empty() && NOTEREF == epubType) {
                hyperlinkType = TextKind::FOOTNOTE;
            }

            if (link[0] == '#') {
                link = reader.myReferenceAlias + link;
            } else {
                link = reader.normalizedReference(reader.myReferenceDirName + link);
            }
        }
        myHyperlinkStack.push(hyperlinkType);
        getBookEncoder(reader).addHyperlinkControlTag(hyperlinkType, link);
    } else {
        myHyperlinkStack.push(TextKind::REGULAR);
    }
    std::string name = attributes.getValue("name");
    if (name.empty()) {
        getBookEncoder(reader).addInnerLabelTag(
                reader.myReferenceAlias + "#" + MiscUtil::decodeHtmlURL(name)
        );
    }
}

void XHTMLTagHyperlinkAction::doAtEnd(XHTMLReader &reader) {
    TextKind kind = myHyperlinkStack.top();
    if (kind != TextKind::REGULAR) {
        getBookEncoder(reader).addControlTag(kind, false);
    }
    myHyperlinkStack.pop();
}


/*** XHTMLTagControlAction ***/

class XHTMLTagControlAction : public XHTMLTextModeTagAction {

public:
    XHTMLTagControlAction(TextKind control);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);

private:
    TextKind myControl;
};


XHTMLTagControlAction::XHTMLTagControlAction(TextKind control) : myControl(control) {
}

void XHTMLTagControlAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    reader.pushTextKind(myControl);
    getBookEncoder(reader).addControlTag(myControl, true);
}

void XHTMLTagControlAction::doAtEnd(XHTMLReader &reader) {
    getBookEncoder(reader).addControlTag(myControl, false);
}

/*** XHTMLTagParagraphWithControlAction ***/


class XHTMLTagParagraphWithControlAction : public XHTMLTextModeTagAction {

public:
    XHTMLTagParagraphWithControlAction(TextKind control);

    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);

private:
    TextKind myControl;
};


XHTMLTagParagraphWithControlAction::XHTMLTagParagraphWithControlAction(TextKind control)
        : myControl(control) {
}

void XHTMLTagParagraphWithControlAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    if (myControl == TextKind::TITLE &&
        getBookEncoder(reader).getCurParagraphCount() > 1) {
        getBookEncoder(reader).insertEndOfSectionParagraph();
    }

    reader.pushTextKind(myControl);
    beginParagraph(reader);
}

void XHTMLTagParagraphWithControlAction::doAtEnd(XHTMLReader &reader) {
    endParagraph(reader);
}

/*** XHTMLTagPreAction ***/


class XHTMLTagPreAction : public XHTMLTextModeTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagPreAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    reader.myPreformatted = true;
    reader.pushTextKind(TextKind::PREFORMATTED);
    beginParagraph(reader);
}

void XHTMLTagPreAction::doAtEnd(XHTMLReader &reader) {
    endParagraph(reader);
    reader.myPreformatted = false;
}

/*** XHTMLTagOpdsAction ***/

class XHTMLTagOpdsAction : public XHTMLTextModeTagAction {

public:
    void doAtStart(XHTMLReader &reader, Attributes &attributes);

    void doAtEnd(XHTMLReader &reader);
};

void XHTMLTagOpdsAction::doAtStart(XHTMLReader &reader, Attributes &attributes) {
    getBookEncoder(reader).addExtensionTag("opds", attributes.getAttributeMap());
}

void XHTMLTagOpdsAction::doAtEnd(XHTMLReader &reader) {
}


/***************************XHTMLReader*******************************************/

XHTMLReader::XHTMLReader(BookEncoder &modelReader, std::shared_ptr<EncryptionMap> map)
        : myModelReader(
        modelReader), myEncryptionMap(map) {
    myMarkNextImageAsCover = false;
    // 获取解析器
    mParser = SAXParserFactory::getParser();
}

XHTMLReader::~XHTMLReader() {
    // ourTagActions 是静态变量，Action 没必要销毁
}

void XHTMLReader::setMarkFirstImageAsCover() {
    myMarkNextImageAsCover = true;
}

void XHTMLReader::addAction(const std::string &tag, XHTMLTagAction *action) {
    ourTagActions[tag] = action;
}

void
XHTMLReader::addAction(const std::string &ns, const std::string &name, XHTMLTagAction *action) {
    std::shared_ptr<NsXMLFilter> predicate = std::make_shared<NsXMLFilter>(ns, name);
    ourNsTagActions[predicate] = action;
}

XHTMLTagAction *XHTMLReader::getAction(const std::string &tag) {
    const std::string lTag = UnicodeUtil::toLower(tag);
    XHTMLTagAction *action = ourTagActions[lTag];
    if (action != nullptr) {
        return action;
    }
    // TODO:感觉逻辑有点复杂
    // 获取所有的 namespace tag 查看 tag 的前缀是否存在当前 namespace 中 (这个会重复遍历)
    // 如果存在，再用 tag 匹配 namespace 的 name
    // 如果不存在，则直接 return。
    for (auto it = ourNsTagActions.begin(); it != ourNsTagActions.end(); ++it) {
        if (it->first->accept(*this, lTag)) {
            return it->second;
        }
    }
    return 0;
}

void XHTMLReader::readFile(const File &file, const std::string &referenceName) {
    initXHTMLTags();

    // 获取目录前缀
    myPathPrefix = MiscUtil::htmlDirectoryPrefix(file.getPath());
    // 方法别名
    myReferenceAlias = fileAlias(referenceName);
    // 超链接标签
    myModelReader.addInnerLabelTag(myReferenceAlias);

    const int index = referenceName.rfind('/', referenceName.length() - 1);
    myReferenceDirName = referenceName.substr(0, index + 1);

    myPreformatted = false;
    myNewParagraphInProgress = false;
    myReadState = XHTML_READ_NOTHING;
    myBodyCounter = 0;
    myCurrentParagraphIsEmpty = true;

    myStyleSheetTable.clear();
    myFontMap = std::make_shared<FontMap>();
    myTagDataStack.clear();

    myStyleParser = std::make_shared<StyleSheetSingleStyleParser>(myPathPrefix);
    myTableParser.reset();

    // 进行解析操作
    // TODO:需要加密处理
    // 源代码： readDocument(file.inputStream(myEncryptionMap));
    mParser->parse(file, *this);
}

void XHTMLReader::initXHTMLTags() {
    if (ourTagActions.empty()) {
        //addAction("html", new XHTMLTagAction());
        addAction("body", new XHTMLTagBodyAction());
        //addAction("title", new XHTMLTagAction());
        //addAction("meta", new XHTMLTagAction());
        //addAction("script", new XHTMLTagAction());

        addAction("aside", new XHTMLTagPseudoSectionAction());

        //addAction("font", new XHTMLTagAction());
        addAction("style", new XHTMLTagStyleAction());

        addAction("p", new XHTMLTagParagraphAction(TextKind::XHTML_TAG_P));
        addAction("h1", new XHTMLTagParagraphWithControlAction(TextKind::H1));
        addAction("h2", new XHTMLTagParagraphWithControlAction(TextKind::H2));
        addAction("h3", new XHTMLTagParagraphWithControlAction(TextKind::H3));
        addAction("h4", new XHTMLTagParagraphWithControlAction(TextKind::H4));
        addAction("h5", new XHTMLTagParagraphWithControlAction(TextKind::H5));
        addAction("h6", new XHTMLTagParagraphWithControlAction(TextKind::H6));

        addAction("ol", new XHTMLTagListAction(1));
        addAction("ul", new XHTMLTagListAction(0));
        //addAction("dl", new XHTMLTagAction());
        addAction("li", new XHTMLTagItemAction());

        addAction("strong", new XHTMLTagControlAction(TextKind::STRONG));
        addAction("b", new XHTMLTagControlAction(TextKind::BOLD));
        addAction("em", new XHTMLTagControlAction(TextKind::EMPHASIS));
        addAction("i", new XHTMLTagControlAction(TextKind::ITALIC));
        addAction("code", new XHTMLTagControlAction(TextKind::CODE));
        addAction("tt", new XHTMLTagControlAction(TextKind::CODE));
        addAction("kbd", new XHTMLTagControlAction(TextKind::CODE));
        addAction("var", new XHTMLTagControlAction(TextKind::CODE));
        addAction("samp", new XHTMLTagControlAction(TextKind::CODE));
        addAction("cite", new XHTMLTagControlAction(TextKind::CITE));
        addAction("sub", new XHTMLTagControlAction(TextKind::SUB));
        addAction("sup", new XHTMLTagControlAction(TextKind::SUP));
        addAction("dd", new XHTMLTagControlAction(TextKind::DEFINITION_DESCRIPTION));
        addAction("dfn", new XHTMLTagControlAction(TextKind::DEFINITION));
        addAction("strike", new XHTMLTagControlAction(TextKind::STRIKETHROUGH));

        addAction("a", new XHTMLTagHyperlinkAction());

        addAction("img", new XHTMLTagImageAction("src"));
        addAction("object", new XHTMLTagImageAction("data"));
        XHTMLSvgImageFilter *predicate = new XHTMLSvgImageFilter();
        addAction("svg", new XHTMLTagSvgAction(predicate));
        addAction("image", new XHTMLTagImageAction(predicate));

        // TODO:添加带有 namespace 的 Action
        addAction(XMLNamespace::Svg, "svg", new XHTMLTagSvgAction(predicate));
        addAction(XMLNamespace::Svg, "image", new XHTMLTagImageAction(predicate));
        addAction(XMLNamespace::FBReaderXhtml, "opds", new XHTMLTagOpdsAction());

        //addAction("area", new XHTMLTagAction());
        //addAction("map", new XHTMLTagAction());

        //addAction("base", new XHTMLTagAction());
        //addAction("blockquote", new XHTMLTagAction());
        //addAction("br", new XHTMLTagRestartParagraphAction());
        //addAction("center", new XHTMLTagAction());
        addAction("div", new XHTMLTagParagraphAction());
        addAction("dt", new XHTMLTagParagraphAction());
        //addAction("head", new XHTMLTagAction());
        //addAction("hr", new XHTMLTagAction());
        addAction("link", new XHTMLTagLinkAction());
        //addAction("param", new XHTMLTagAction());
        //addAction("q", new XHTMLTagAction());
        //addAction("s", new XHTMLTagAction());

        addAction("pre", new XHTMLTagPreAction());
        //addAction("big", new XHTMLTagAction());
        //addAction("small", new XHTMLTagAction());
        //addAction("u", new XHTMLTagAction());

        //addAction("table", new XHTMLTagAction());
        addAction("td", new XHTMLTagParagraphAction());
        addAction("th", new XHTMLTagParagraphAction());
        //addAction("tr", new XHTMLTagAction());
        //addAction("caption", new XHTMLTagAction());
        //addAction("span", new XHTMLTagAction());

        addAction("video", new XHTMLTagVideoAction());
        addAction("source", new XHTMLTagSourceAction());
    }
}


const XHTMLTagList &XHTMLReader::tagInfos(size_t depth) const {
    if (myTagDataStack.size() < depth + 2) {
        return EMPTY_INFO_LIST;
    }
    return myTagDataStack[myTagDataStack.size() - depth - 2]->Children;
}

bool
XHTMLReader::matches(const std::shared_ptr<CSSSelector::Component> next, int depth, int pos) const {
    if (next == nullptr) {
        return true;
    }

    // TODO: check next->Selector.Next
    const CSSSelector &selector = *(next->Selector);
    switch (next->Delimiter) {
        default:
            return false;
        case CSSSelector::Parent:
            return tagInfos(depth + 1).matches(selector, -1) && matches(selector.Next, depth + 1);
        case CSSSelector::Ancestor:
            if (selector.Next || selector.Next->Delimiter == CSSSelector::Ancestor) {
                for (size_t i = 1; i < myTagDataStack.size() - depth - 1; ++i) {
                    if (tagInfos(depth + i).matches(selector, -1)) {
                        return matches(selector.Next, i);
                    }
                }
                return false;
            } else {
                for (size_t i = 1; i < myTagDataStack.size() - depth - 1; ++i) {
                    if (tagInfos(depth + i).matches(selector, -1) && matches(selector.Next, i)) {
                        return true;
                    }
                }
                return false;
            }
        case CSSSelector::Predecessor:
            if (selector.Next != nullptr && selector.Next->Delimiter == CSSSelector::Previous) {
                while (true) {
                    // it is guaranteed that pos will be decreased on each step
                    pos = tagInfos(depth).find(selector, 1, pos);
                    if (pos == -1) {
                        return false;
                    } else if (matches(selector.Next, depth, pos)) {
                        return true;
                    }
                }
            } else {
                const int index = tagInfos(depth).find(selector, 0, pos);
                return index != -1 && matches(selector.Next, depth, index);
            }
        case CSSSelector::Previous:
            return tagInfos(depth).matches(selector, pos - 1) &&
                   matches(selector.Next, depth, pos - 1);
    }
}

void XHTMLReader::applySingleEntry(std::shared_ptr<TextStyleTag> entry) {
    if (entry == nullptr) {
        return;
    }
    addTextStyleEntry(*(entry->start()), myTagDataStack.size());
    std::shared_ptr<TagData> data = myTagDataStack.back();
    data->StyleEntries.push_back(entry);
    const TextDisplayCode dc = entry->displayCode();
    if (dc != TextDisplayCode::DC_NOT_DEFINED) {
        data->DisplayCode = dc;
    }
}

void XHTMLReader::applyTagStyles(const std::string &tag, const std::string &aClass) {
    std::vector<std::pair<CSSSelector, std::shared_ptr<TextStyleTag> > > controls =
            myStyleSheetTable.allControls(tag, aClass);
    for (auto it = controls.begin(); it != controls.end();
         ++it) {
        if (matches(it->first.Next)) {
            applySingleEntry(it->second);
        }
    }
}

void XHTMLReader::addTextStyleEntry(const TextStyleTag &entry, unsigned char depth) {
    if (!entry.isFeatureSupported(TextFeature::FONT_FAMILY)) {
        myModelReader.addStyleTag(entry, depth);
        return;
    }

    bool doFixFamiliesList = false;

    const std::vector<std::string> &families = entry.fontFamilies();
    for (auto it = families.begin(); it != families.end(); ++it) {
        Logger::i("FONT", "Requested font family: " + *it);
        std::shared_ptr<FontEntry> fontEntry = myFontMap->get(*it);
        if (fontEntry != nullptr) {
            const std::string realFamily = myModelReader.addFontTag(*it, fontEntry);
            if (realFamily != *it) {
                Logger::i("FONT", "Entry for " + *it + " stored as " + realFamily);
                doFixFamiliesList = true;
                break;
            }
        }
    }

    if (!doFixFamiliesList) {
        myModelReader.addStyleTag(entry, depth);
    } else {
        std::vector<std::string> realFamilies;
        for (std::vector<std::string>::const_iterator it = families.begin();
             it != families.end(); ++it) {
            std::shared_ptr<FontEntry> fontEntry = myFontMap->get(*it);
            if (fontEntry != nullptr) {
                realFamilies.push_back(myModelReader.addFontTag(*it, fontEntry));
            } else {
                realFamilies.push_back(*it);
            }
        }
        myModelReader.addStyleTag(entry, realFamilies, depth);
    }
}

void XHTMLReader::beginParagraph(bool restarted) {
    myCurrentParagraphIsEmpty = true;
    myModelReader.beginParagraph();
    for (auto it = myTagDataStack.begin(); it != myTagDataStack.end(); ++it) {
        const std::vector<TextKind> &kinds = (*it)->TextKinds;
        for (std::vector<TextKind>::const_iterator jt = kinds.begin(); jt != kinds.end(); ++jt) {
            myModelReader.addControlTag(*jt, true);
        }

        const std::vector<std::shared_ptr<TextStyleTag> > &entries = (*it)->StyleEntries;
        bool inheritedOnly = !restarted || it + 1 != myTagDataStack.end();
        const unsigned char depth = it - myTagDataStack.begin() + 1;
        for (std::vector<std::shared_ptr<TextStyleTag> >::const_iterator jt = entries.begin();
             jt != entries.end();
             ++jt) {
            std::shared_ptr<TextStyleTag> entry = inheritedOnly ? (*jt)->inherited()
                                                                : (*jt)->start();
            addTextStyleEntry(*entry, depth);
        }
    }
}

void XHTMLReader::endParagraph() {
    myModelReader.endParagraph();
}

void XHTMLReader::restartParagraph(bool addEmptyLine) {
    if (addEmptyLine && myCurrentParagraphIsEmpty) {
        myModelReader.addFixedHSpaceTag(1);
    }
    const unsigned char depth = myTagDataStack.size();
    TextStyleTag spaceAfterBlocker(TextTagType::STYLE_OTHER);
    spaceAfterBlocker.setLength(
            TextFeature::LENGTH_SPACE_AFTER,
            0,
            TextSizeUnit::SIZE_UNIT_PIXEL
    );
    addTextStyleEntry(spaceAfterBlocker, depth);
    endParagraph();
    beginParagraph(true);
    TextStyleTag spaceBeforeBlocker(TextTagType::STYLE_OTHER);
    spaceBeforeBlocker.setLength(
            TextFeature::LENGTH_SPACE_BEFORE,
            0,
            TextSizeUnit::SIZE_UNIT_PIXEL
    );
    addTextStyleEntry(spaceBeforeBlocker, depth);
}

void XHTMLReader::pushTextKind(TextKind kind) {
    if (kind != TextKind::NONE) {
        myTagDataStack.back()->TextKinds.push_back(kind);
    }
}

bool XHTMLReader::processNamespaces() const {
    return true;
}

const std::string XHTMLReader::normalizedReference(const std::string &reference) const {
    const std::size_t index = reference.find('#');
    if (index == std::string::npos) {
        return fileAlias(reference);
    } else {
        return fileAlias(reference.substr(0, index)) + reference.substr(index);
    }
}

const std::string &XHTMLReader::fileAlias(const std::string &fileName) const {
    auto it = myFileNumbers.find(fileName);
    if (it != myFileNumbers.end()) {
        return it->second;
    }

    // 将文件中的 url 转化成正确的文件名
    const std::string correctedFileName =
            FilePathUtil::normalizeUnixPath(MiscUtil::decodeHtmlURL(fileName));
    it = myFileNumbers.find(correctedFileName);

    if (it != myFileNumbers.end()) {
        return it->second;
    }

    std::string num;
    StringUtil::appendNumber(num, myFileNumbers.size());
    myFileNumbers.insert(std::make_pair(correctedFileName, num));
    it = myFileNumbers.find(correctedFileName);
    return it->second;
}

void
XHTMLReader::startElement(std::string &localName, std::string &fullName, Attributes &attributes) {
    const std::string sTag = UnicodeUtil::toLower(fullName);

    if (sTag == "br") {
        restartParagraph(true);
        return;
    }

    /**
     * 检测 class 参数
     */
    std::vector<std::string> classesList;

    const std::string aClasses = attributes.getValue("class");

    if (!aClasses.empty()) {
        const std::vector<std::string> split = StringUtil::split(aClasses, " ", true);
        for (std::vector<std::string>::const_iterator it = split.begin(); it != split.end(); ++it) {
            classesList.push_back(*it);
        }
    }

    if (!myTagDataStack.empty()) {
        myTagDataStack.back()->Children.push_back(XHTMLTag(sTag, classesList));
    }

    myTagDataStack.push_back(std::make_shared<TagData>());
    TagData &tagData = *myTagDataStack.back();

    /**
     * 检测 id 参数
     */
    static const std::string HASH = "#";
    const std::string id = attributes.getValue("id");
    if (!id.empty()) {
        myModelReader.addInnerLabelTag(myReferenceAlias + HASH + id);
    }

    Boolean breakBefore = myStyleSheetTable.doBreakBefore(sTag, EMPTY);
    tagData.PageBreakAfter = myStyleSheetTable.doBreakAfter(sTag, EMPTY);

    for (auto it = classesList.begin(); it != classesList.end(); ++it) {
        const Boolean bb = myStyleSheetTable.doBreakBefore(sTag, *it);
        if (bb != Boolean::UNDEFINED) {
            breakBefore = bb;
        }
        const Boolean ba = myStyleSheetTable.doBreakAfter(sTag, *it);
        if (ba != Boolean::UNDEFINED) {
            tagData.PageBreakAfter = ba;
        }
    }

    if (breakBefore == Boolean::TRUE) {
        myModelReader.insertEndOfSectionParagraph();
    }

    /**
     * 根据标签名，获取对应的 action
     */

    // 启动 action 执行相应操作
    XHTMLTagAction *action = getAction(sTag);
    if (action != nullptr && action->isEnabled(myReadState)) {
        action->doAtStart(*this, attributes);
    }

    applyTagStyles(ANY, EMPTY);
    applyTagStyles(sTag, EMPTY);

    for (auto it = classesList.begin(); it != classesList.end(); ++it) {
        applyTagStyles(EMPTY, *it);
        applyTagStyles(sTag, *it);
    }

    /**
     * 根据标签名，获取对应的 style
     */
    const std::string style = attributes.getValue("style");

    if (!style.empty()) {
        applySingleEntry(myStyleParser->parseSingleEntry(style.c_str()));


        if (tagData.DisplayCode == TextDisplayCode::DC_BLOCK) {
            restartParagraph(false);
        }
    }
}

void XHTMLReader::characterData(std::string &data) {
    const char *text = data.c_str();
    size_t textLen = data.length();

    switch (myReadState) {
        case XHTML_READ_NOTHING:
        case XHTML_READ_VIDEO:
            break;
        case XHTML_READ_STYLE:
            if (myTableParser != nullptr) {
                myTableParser->parseString(text, textLen);
            }
            break;
        case XHTML_READ_BODY:
            if (myPreformatted) {
                if (*text == '\r' || *text == '\n') {
                    restartParagraph(true);
                    text += 1;
                    textLen -= 1;
                }
                std::size_t spaceCounter = 0;
                while (spaceCounter < textLen &&
                       std::isspace((unsigned char) *(text + spaceCounter))) {
                    ++spaceCounter;
                }
                myModelReader.addFixedHSpaceTag(spaceCounter);
                text += spaceCounter;
                textLen -= spaceCounter;
            } else if (myNewParagraphInProgress || !myModelReader.hasParagraphOpen()) {
                while (std::isspace((unsigned char) *text)) {
                    ++text;
                    if (--textLen == 0) {
                        break;
                    }
                }
            }
            if (textLen > 0) {
                myCurrentParagraphIsEmpty = false;
                if (!myModelReader.hasParagraphOpen()) {
                    myModelReader.beginParagraph();
                }
                myModelReader.addText(data);
                myNewParagraphInProgress = false;
            }
            break;
    }
}

void XHTMLReader::endElement(std::string &localName, std::string &fullName) {
    const std::string sTag = UnicodeUtil::toLower(fullName);

    if (sTag == "br") {
        return;
    }

    const TagData &tagData = *myTagDataStack.back();
    const std::vector<std::shared_ptr<TextStyleTag> > &entries = tagData.StyleEntries;
    size_t entryCount = entries.size();
    const unsigned char depth = myTagDataStack.size();
    for (auto jt = entries.begin(); jt != entries.end(); ++jt) {
        std::shared_ptr<TextStyleTag> entry = *jt;
        std::shared_ptr<TextStyleTag> endEntry = entry->end();
        if (endEntry != nullptr) {
            addTextStyleEntry(*endEntry, depth);
            ++entryCount;
        }
    }

    XHTMLTagAction *action = getAction(sTag);
    if (action != nullptr && action->isEnabled(myReadState)) {
        action->doAtEnd(*this);
        myNewParagraphInProgress = false;
    }

    for (; entryCount > 0; --entryCount) {
        myModelReader.addStyleCloseTag();
    }

    if (tagData.PageBreakAfter == Boolean::TRUE) {
        myModelReader.insertEndOfSectionParagraph();
    } else if (tagData.DisplayCode == TextDisplayCode::DC_BLOCK) {
        restartParagraph(false);
    }

    myTagDataStack.pop_back();
}

void XHTMLReader::error(std::string &err) {
    // TODO：输出错误处理
}

XHTMLReader::TagData::TagData() : PageBreakAfter(Boolean::UNDEFINED),
                                  DisplayCode(TextDisplayCode::DC_INLINE) {
}
