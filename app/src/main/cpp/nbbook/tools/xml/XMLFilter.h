// author : newbiechen
// date : 2020-02-18 23:22
// description : 用于过滤 xml 标签信息
//

#ifndef NBREADER_XMLFILTER_H
#define NBREADER_XMLFILTER_H

#include <string>
#include "NsSAXHandler.h"

// 过滤带有 namespace 的 xml 标签
class NsXMLFilter {
public:
    /**
     * @param url：命名空间对应的 url 地址
     * @param patternName：匹配的名字
     */
    NsXMLFilter(const std::string &nsUrl, const std::string &attrName);

    virtual bool accept(const NsSAXHandler &handler, const char *patternName) const;

    /**
     *
     * @param handler ：必须支持 NsSAXHandler
     * @param checkName：待检测的名字
     * @return
     */
    virtual bool accept(const NsSAXHandler &handler, const std::string &patternName) const;

    std::string pattern(const NsSAXHandler &handler, const Attributes &attrs) const;


    const std::string &getAttributetName() {
        return mAttrName;
    }

private:
    const std::string mNsUrl;
    const std::string mAttrName;
};


#endif //NBREADER_XMLFILTER_H
