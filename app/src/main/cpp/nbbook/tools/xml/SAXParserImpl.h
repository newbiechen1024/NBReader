// author : newbiechen
// date : 2020-02-15 15:04
// description : xml 解析器
//

#ifndef NBREADER_SAXPARSERIMPL_H
#define NBREADER_SAXPARSERIMPL_H

#include <expat/expat.h>
#include "../../filesystem/File.h"
#include "SAXParser.h"

class SAXParserImpl : public SAXParser {

public:
    SAXParserImpl();

    ~SAXParserImpl();

    void parse(std::shared_ptr<InputStream> &is, SAXHandler &handler) override;

    void reset() override;

    void startNamespace(const char *prefix, const char *uri);

    void endNamespace(const char *prefix);

    void startElement(const char *name, const char **attributes);

    void characterData(const char *text, int len);

    void endElement(const char *name);

private:
    // 初始化解析器
    void initParser();

    // 解析缓冲区数据
    bool parseBuffer(const char *buffer, std::size_t len);

    // 检查状态
    bool checkState();

private:
    // xml 解析器
    XML_Parser mXmlParser;
    // 是否初始化
    bool isInitialized;
    // 解析缓冲
    char *mParseBuffer;
    // 待解析文件
    std::shared_ptr<InputStream> mParseStream;
    // 待处理的 Handler 回调
    SAXHandler *mParserHandler;
};


#endif //NBREADER_SAXPARSERIMPL_H
