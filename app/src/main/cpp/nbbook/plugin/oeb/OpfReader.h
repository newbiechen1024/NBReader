// author : newbiechen
// date : 2020-02-23 10:48
// description : 解析 Opf 文件信息
//

#ifndef NBREADER_OPFREADER_H
#define NBREADER_OPFREADER_H

#include "../../tools/xml/SAXHandler.h"
#include "../../tools/xml/BaseHandler.h"

class OpfReader : public BaseHandler {
public:
    void readFile(File &file);

    /**
     * 返回 .ncx 文件名
     * @return
     */
    std::string getNcxFileName() const {
        return myNCXTOCFileName;
    }

    std::string getOpfDirPath() const {
        return myOpfDirPath;
    }

    virtual void
    startElement(std::string &localName, std::string &fullName, Attributes &attributes);

    virtual void endElement(std::string &localName, std::string &fullName);

    virtual void error(std::string &err);

protected:

    bool isOpfTag(const std::string &expectTag, const std::string &actualTag) const;

    bool isDcTag(const std::string &expectTag, const std::string &actualTag) const;

    bool isMetadataTag(const std::string &tagName);

private:
    enum ReaderState {
        READ_NONE,
        READ_MANIFEST,
        READ_SPINE,
        READ_GUIDE,
        READ_TOUR
    };

    ReaderState myState;
    std::string myOpfDirPath;
    std::shared_ptr<EncryptionMap> myEncryptionMap;
    std::map<std::string, std::string> myIdToHref;
    std::map<std::string, std::string> myHrefToMediatype;
    std::vector<std::string> myHtmlFileNames;
    std::string myNCXTOCFileName;
    std::string myCoverFileName;
    std::string myCoverFileType;
    std::string myCoverMimeType;
    std::vector<std::pair<std::string, std::string> > myTourTOC;
    std::vector<std::pair<std::string, std::string> > myGuideTOC;
};


#endif //NBREADER_OPFREADER_H
