// author : newbiechen
// date : 2020-02-20 23:49
// description : 
//

#include "ContainerReader.h"
#include "../../util/UnicodeUtil.h"
#include "../../tools/xml/SAXParserFactory.h"

void ContainerReader::readFile(const File &path) {
    auto parser = SAXParserFactory::getParser();
    parser->parse(path, *this);
}

void ContainerReader::startElement(std::string &localName, std::string &fullName,
                                   Attributes &attributes) {
    const std::string tagString = UnicodeUtil::toLower(fullName);
    if (tagString == "rootfile") {
        // 这是指向 opf path 的路径
        std::string fullPath = attributes.getValue("full-path");
        if (!fullPath.empty()) {
            opfPath = fullPath;
            interrupt();
        }
    }
}