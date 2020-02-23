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

#ifndef __XHTMLREADER_H__
#define __XHTMLREADER_H__

#include <string>
#include <map>
#include <vector>
#include <stack>

#include "../css/StyleSheetTable.h"
#include "../css/StyleSheetParser.h"
#include "../xml/SAXHandler.h"
#include "../../reader/text/tag/TextKind.h"
#include "../../reader/text/tag/VideoTag.h"
#include "XHTMLTag.h"
#include "../xml/SAXParser.h"
#include "../xml/BaseHandler.h"
#include "../xml/XMLFilter.h"

class BookEncoder;

class File;

class XHTMLReader;

class EncryptionMap;

enum XHTMLReadingState {
    XHTML_READ_NOTHING,
    XHTML_READ_STYLE,
    XHTML_READ_BODY,
    XHTML_READ_VIDEO
};

// 标签行为
class XHTMLTagAction {

public:
    virtual ~XHTMLTagAction();

    virtual void doAtStart(XHTMLReader &reader, Attributes &attributes) = 0;

    virtual void doAtEnd(XHTMLReader &reader) = 0;

    virtual bool isEnabled(XHTMLReadingState state) = 0;

protected:
    static BookEncoder &getBookEncoder(XHTMLReader &reader);

    static const std::string &pathPrefix(XHTMLReader &reader);

    static void beginParagraph(XHTMLReader &reader);

    static void endParagraph(XHTMLReader &reader);
};

class XHTMLReader : public BaseHandler {

public:
    struct TagData {
        std::vector<TextKind> TextKinds;
        std::vector<std::shared_ptr<TextStyleTag> > StyleEntries;
        Boolean PageBreakAfter;
        TextDisplayCode DisplayCode;
        XHTMLTagList Children;

        TagData();
    };

public:
    static void addAction(const std::string &tag, XHTMLTagAction *action);

    static void
    addAction(const std::string &ns, const std::string &name, XHTMLTagAction *action);

    static void initXHTMLTags();


private:
    static std::map<std::string, XHTMLTagAction *> ourTagActions;
    static std::map<std::shared_ptr<NsXMLFilter>, XHTMLTagAction *>
            ourNsTagActions;

public:
    void
    startElement(std::string &localName, std::string &fullName, Attributes &attributes) override;

    void characterData(std::string &data) override;

    void endElement(std::string &localName, std::string &fullName) override;

    void error(std::string &err) override;

public:
    XHTMLReader(BookEncoder &modelReader, std::shared_ptr<EncryptionMap> map);

    ~XHTMLReader();


    void readFile(const File &file, const std::string &referenceName);

    const std::string &fileAlias(const std::string &fileName) const;

    const std::string normalizedReference(const std::string &reference) const;

    void setMarkFirstImageAsCover();

private:
    XHTMLTagAction *getAction(const std::string &tag);

    bool processNamespaces() const;

    void beginParagraph(bool restarted = false);

    void endParagraph();

    void restartParagraph(bool addEmptyLine);

    const XHTMLTagList &tagInfos(size_t depth) const;

    bool
    matches(const std::shared_ptr<CSSSelector::Component> next, int depth = 0, int pos = -1) const;

    void applySingleEntry(std::shared_ptr<TextStyleTag> entry);

    void applyTagStyles(const std::string &tag, const std::string &aClass);

    void addTextStyleEntry(const TextStyleTag &entry, unsigned char depth);

    void pushTextKind(TextKind kind);

private:
    mutable std::map<std::string, std::string> myFileNumbers;

    BookEncoder &myModelReader;
    std::shared_ptr<EncryptionMap> myEncryptionMap;
    std::shared_ptr<SAXParser> mParser;
    std::string myPathPrefix;
    std::string myReferenceAlias;
    std::string myReferenceDirName;
    bool myPreformatted;
    bool myNewParagraphInProgress;
    StyleSheetTable myStyleSheetTable;
    std::shared_ptr<FontMap> myFontMap;
    std::vector<std::shared_ptr<TagData> >
            myTagDataStack;
    bool myCurrentParagraphIsEmpty;
    std::shared_ptr<StyleSheetSingleStyleParser> myStyleParser;
    std::shared_ptr<StyleSheetTableParser> myTableParser;
    std::map<std::string, std::shared_ptr<StyleSheetParserWithCache> >
            myFileParsers;
    XHTMLReadingState myReadState;
    int myBodyCounter;
    std::stack<int> myListNumStack;
    bool myMarkNextImageAsCover;
    std::shared_ptr<VideoTag> myVideoEntry;

    friend class XHTMLTagAction;

    friend class XHTMLTagStyleAction;

    friend class XHTMLTagLinkAction;

    friend class XHTMLTagHyperlinkAction;

    friend class XHTMLTagPreAction;

    friend class XHTMLTagParagraphAction;

    friend class XHTMLTagParagraphWithControlAction;

    friend class XHTMLTagControlAction;

    friend class XHTMLTagBodyAction;

    friend class XHTMLTagListAction;

    friend class XHTMLTagItemAction;

    friend class XHTMLTagImageAction;

    friend class XHTMLTagVideoAction;

    friend class XHTMLTagSourceAction;
};

#endif /* __XHTMLREADER_H__ */
