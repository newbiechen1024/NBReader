// author : newbiechen
// date : 2020-02-15 16:15
// description : 
//

#ifndef NBREADER_SAXPARSER_H
#define NBREADER_SAXPARSER_H


#include <memory>
#include "../../filesystem/io/InputStream.h"
#include "SAXHandler.h"
#include "../../filesystem/File.h"

class SAXParser {
public:
    SAXParser() {
    };

    virtual ~SAXParser() {
    };

    void parse(const std::string &path, SAXHandler &handler);

    void parse(const File &file, SAXHandler &handler);

    virtual void parse(std::shared_ptr<InputStream> &is, SAXHandler &handler) = 0;

    virtual void reset() = 0;
};

#endif //NBREADER_SAXPARSER_H
