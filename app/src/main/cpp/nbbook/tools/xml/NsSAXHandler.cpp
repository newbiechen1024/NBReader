// author : newbiechen
// date : 2020-02-18 23:27
// description : 
//

#include "NsSAXHandler.h"

void NsSAXHandler::startNamespace(std::string &prefix, std::string &uri) {
    mNamespaceMap[prefix] = uri;
}

void NsSAXHandler::endNamespace(std::string &prefix) {
}
