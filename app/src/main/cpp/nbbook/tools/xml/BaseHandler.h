// author : newbiechen
// date : 2020-02-18 23:27
// description : 对经常用到的 XML 解析功能的封装
//

#ifndef NBREADER_BASEHANDLER_H
#define NBREADER_BASEHANDLER_H


#include "SAXHandler.h"

class BaseHandler : public SAXHandler {

public:
    bool isNamespaceExist(const std::string &ns) const {
        return mNamespaceMap.find(ns) != mNamespaceMap.end();
    }

    std::string getNamespaceUrl(const std::string &ns) const {
        auto it = mNamespaceMap.find(ns);
        if (it != mNamespaceMap.end()) {
            return it->second;
        }
        return std::string();
    }

    void startNamespace(std::string &prefix, std::string &uri) override;

    void endNamespace(std::string &prefix) override;

    /**
     * 是否是正确的 tag
     * @return
     */
    bool isRightTag(const std::string &ns, const std::string &expectTag, const std::string &actualTag) const;

private:
    // 存储 namespace 的容器
    std::map<std::string, std::string> mNamespaceMap;
};


#endif //NBREADER_BASEHANDLER_H
