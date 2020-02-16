// author : newbiechen
// date : 2020-02-15 16:15
// description : 
//

#include "SAXParser.h"

void SAXParser::parse(const std::string &path, SAXHandler &handler) {
    File parseFile(path);

    if (!parseFile.exists()) {
        // todo:抛出异常
    } else {
        parse(parseFile, handler);
    }
}

void SAXParser::parse(File &file, SAXHandler &handler) {
    auto is = file.getInputStream();
    parse(is, handler);
}