// author : newbiechen
// date : 2020-02-20 23:49
// description : 
//

#ifndef NBREADER_CONTAINERREADER_H
#define NBREADER_CONTAINERREADER_H

#include "../../tools/xml/SAXHandler.h"

class ContainerReader : public SAXHandler {

public:
    const std::string &getOpfPath() const {
        return opfPath;
    }

    void readFile(const File &path);

protected:
    void
    startElement(std::string &localName, std::string &fullName, Attributes &attributes) override;

private:
    std::string opfPath;
};


#endif //NBREADER_CONTAINERREADER_H
