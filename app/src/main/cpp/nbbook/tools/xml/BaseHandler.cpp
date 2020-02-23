// author : newbiechen
// date : 2020-02-18 23:27
// description : 
//

#include "BaseHandler.h"
#include "../../util/StringUtil.h"

void BaseHandler::startNamespace(std::string &prefix, std::string &uri) {
    mNamespaceMap[prefix] = uri;
}

void BaseHandler::endNamespace(std::string &prefix) {
}


bool BaseHandler::isRightTag(const std::string &ns, const std::string &expectTag,
                             const std::string &actualTag) const {
    if (expectTag == actualTag) {
        return true;
    }

    const int nameLen = expectTag.size();
    const int tagLen = actualTag.size();
    if (tagLen < nameLen + 2) {
        return false;
    }
    if (StringUtil::endsWith(expectTag, actualTag) && actualTag[tagLen - nameLen - 1] == ':') {
        std::string ns = getNamespaceUrl(actualTag.substr(0, tagLen - nameLen - 1));
        return !ns.empty();
    }
    return false;
}