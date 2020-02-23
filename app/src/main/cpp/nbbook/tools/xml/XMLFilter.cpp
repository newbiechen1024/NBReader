// author : newbiechen
// date : 2020-02-18 23:22
// description : 
//

#include "XMLFilter.h"


NsXMLFilter::NsXMLFilter(const std::string &nsUrl, const std::string &attrName) : mNsUrl(nsUrl),
                                                                                  mAttrName(
                                                                                          attrName) {
}

bool NsXMLFilter::accept(const BaseHandler &handler, const char *checkName) const {
    return accept(handler, std::string(checkName));
}

bool NsXMLFilter::accept(const BaseHandler &handler, const std::string &patternName) const {
    const std::size_t index = patternName.find(':');
    const std::string namespaceId =
            index == std::string::npos ? std::string() : patternName.substr(0, index);


    return patternName.substr(index + 1) == mAttrName
           && handler.isNamespaceExist(namespaceId)
           && handler.getNamespaceUrl(namespaceId) == mNsUrl;
}

std::string NsXMLFilter::pattern(const BaseHandler &handler, const Attributes &attrs) const {
    size_t attrsCount = attrs.getLength();
    std::string attr;

    for (int i = 0; i < attrsCount; ++i) {
        attr = attrs.getValue(i);
        if (accept(handler, attr)) {
            return attr;
        }
    }

    return std::string();
}
