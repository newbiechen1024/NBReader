// author : newbiechen
// date : 2020-02-15 15:16
// description : xml 解析回调
//

#ifndef NBREADER_SAXHANDLER_H
#define NBREADER_SAXHANDLER_H

#include <string>
#include <cstdlib>
#include <map>
#include "SAXParserImpl.h"

class Attributes {
public:
    Attributes(const char **data);

public:
    // 获取参数个数
    size_t getLength() const {
        return length;
    }

    std::string getKey(int index) const;

    /**
     * 根据索引获取值
     * @param index
     * @return
     */
    std::string getValue(int index) const;

    /**
     * 根据 key 获取 value
     * @param key
     * @return
     */
    std::string getValue(const std::string &key) const;

    std::map<std::string, std::string> getAttributeMap();

private:
    const char **data;
    size_t length;
};

class SAXHandler {
public:
    SAXHandler() {
        isInterrupted = false;
    }

    ~SAXHandler() {
    }

    bool isInterrupt() {
        return isInterrupted;
    }

    // 停止解析
    void interrupt() {
        isInterrupted = true;
    }

    virtual void startDocument() {};

    virtual void endDocument() {};

    virtual void startNamespace(std::string &prefix, std::string &uri) {
        // TODO：该回调暂未实现
    };


    virtual void endNamespace(std::string &prefix) {
        // TODO：该回调暂未实现
    };

    virtual void startElement(
            std::string &localName,
            std::string &fullName,
            Attributes &attributes) {

    };

    virtual void characterData(std::string &data) {

    };

    virtual void endElement(std::string &localName,
                            std::string &fullName) {
    };

    virtual void error(std::string &err) {

    };

private:
    bool isInterrupted;

    friend SAXParser;
};

#endif //NBREADER_SAXHANDLER_H
