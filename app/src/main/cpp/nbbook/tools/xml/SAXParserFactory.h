// author : newbiechen
// date : 2020-02-15 16:15
// description : 
//

#ifndef NBREADER_SAXPARSERFACTORY_H
#define NBREADER_SAXPARSERFACTORY_H


#include <memory>
#include "SAXParser.h"
#include "SAXParserImpl.h"

class SAXParserFactory {

public:
    static std::shared_ptr<SAXParser> getParser() {
        return std::make_shared<SAXParserImpl>();;
    }

private:
    SAXParserFactory() {
    }
};


#endif //NBREADER_SAXPARSERFACTORY_H
