// author : newbiechen
// date : 2019-09-27 17:41
// description : 为 TextModel 文本添加对应的类型标记
//

#ifndef NBREADER_BOOKREADER_H
#define NBREADER_BOOKREADER_H


#include "BookModel.h"
#include "NBTextMark.h"

class BookReader {
public:
    BookReader(BookModel &model);

    virtual ~BookReader();

    // 设置注解的 textModel，暂时不管
    // void setFootnoteTextModel(const std::string &id);

    void insertEndOfSectionParagraph();

    void insertPseudoEndOfSectionParagraph();

    void insertEndOfTextParagraph();

    void insertEncryptedSectionParagraph();

    void pushTextMark(NBTextMark mark);

    bool popTextMark();

    bool isKindStackEmpty() const;

    void beginParagraph(ZLTextParagraph::Kind kind = ZLTextParagraph::TEXT_PARAGRAPH);

    void endParagraph();

    bool paragraphIsOpen() const;

    void addControl(FBTextKind kind, bool start);

    void addStyleEntry(const ZLTextStyleEntry &entry, unsigned char depth);

    void addStyleEntry(const ZLTextStyleEntry &entry, const std::vector<std::string> &fontFamilies,
                       unsigned char depth);

    void addStyleCloseEntry();

    void addHyperlinkControl(FBTextKind kind, const std::string &label);

    void addHyperlinkLabel(const std::string &label);

    void addHyperlinkLabel(const std::string &label, int paragraphNumber);

    void addFixedHSpace(unsigned char length);

    void addImageReference(const std::string &id, short vOffset, bool isCover);

    void addImage(const std::string &id, shared_ptr<const ZLImage> image);

    void addVideoEntry(const ZLVideoEntry &entry);

    void
    addExtensionEntry(const std::string &action, const std::map<std::string, std::string> &data);

    void beginContentsParagraph(int referenceNumber = -1);

    void endContentsParagraph();

    bool contentsParagraphIsOpen() const;
    //void setReference(std::size_t contentsParagraphNumber, int referenceNumber);

    void addData(const std::string &data);

    void addContentsData(const std::string &data);

    void enterTitle() { myInsideTitle = true; }

    void exitTitle() { myInsideTitle = false; }

    std::string putFontEntry(const std::string &family, shared_ptr<FontEntry> fontEntry);

    const BookModel &model() const { return myModel; }

    void reset();

private:
    void insertEndParagraph(ZLTextParagraph::Kind kind);

    void flushTextBufferToParagraph();

private:
    BookModel &myModel;
    shared_ptr<ZLTextModel> myCurrentTextModel;
    std::list<shared_ptr<ZLTextModel> > myModelsWithOpenParagraphs;

    std::vector<FBTextKind> myKindStack;

    bool myContentsParagraphExists;
    std::stack<shared_ptr<ContentsTree> > myContentsTreeStack;

    bool mySectionContainsRegularContents;
    bool myInsideTitle;

    std::vector<std::string> myBuffer;

    std::string myHyperlinkReference;
    FBHyperlinkType myHyperlinkType;
    FBTextKind myHyperlinkKind;

    shared_ptr<ZLCachedMemoryAllocator> myFootnotesAllocator;
};


#endif //NBREADER_BOOKREADER_H
