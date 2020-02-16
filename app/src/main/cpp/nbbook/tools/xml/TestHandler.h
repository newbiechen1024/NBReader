// author : newbiechen
// date : 2020-02-16 19:01
// description : 
//

#ifndef NBREADER_TESTHANDLER_H
#define NBREADER_TESTHANDLER_H


#include "SAXHandler.h"

class TestHandler : public SAXHandler {

public:
    virtual void startDocument();

    virtual void endDocument();

    virtual void
    startElement(std::string &localName, std::string &fullName, Attributes &attributes);

    virtual void characterData(std::string &data);

    virtual void endElement(std::string &localName, std::string &fullName);

    virtual void error(std::string &err);
};


#endif //NBREADER_TESTHANDLER_H
