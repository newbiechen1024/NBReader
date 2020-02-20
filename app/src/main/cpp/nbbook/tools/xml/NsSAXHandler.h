// author : newbiechen
// date : 2020-02-18 23:27
// description : 处理 namespace 的 SAXHandler
//

#ifndef NBREADER_NSSAXHANDLER_H
#define NBREADER_NSSAXHANDLER_H


#include "SAXHandler.h"
#include "../../../../../../../../../../Library/Android/sdk/ndk-bundle/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/include/c++/v1/string"

class NsSAXHandler : public SAXHandler {

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

protected:
    virtual void startNamespace(std::string &prefix, std::string &uri);

    virtual void endNamespace(std::string &prefix);

private:
    // 存储 namespace 的容器
    std::map<std::string, std::string> mNamespaceMap;
};


#endif //NBREADER_NSSAXHANDLER_H
