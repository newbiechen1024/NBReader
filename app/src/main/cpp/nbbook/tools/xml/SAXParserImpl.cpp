// author : newbiechen
// date : 2020-02-15 15:04
// description : 
//

#include "SAXParserImpl.h"
#include "../../util/StringUtil.h"
#include "../../util/Logger.h"
#include "SAXHandler.h"

static const std::string TAG = "SAXParserImpl";

static const size_t BUFFER_SIZE = 1024 * 8;
static const char TEXT_SPLIT = ':';

// 起始标签回调
static void startElementHandler(void *userData, const char *name, const char **attributes) {
    SAXParserImpl *parser = (SAXParserImpl *) userData;

    // TODO：暂时通过这种方式查找 namespace，之后需要修改逻辑，或者使用 libxml，还是看下 expat 源码到底哪里有问题。。。
    for (const char **a = attributes; (*a != 0) && (*(a + 1) != 0); a += 2) {
        if (std::strncmp(*a, "xmlns", 5) == 0) {
            std::string id;
            // 如果查找到值
            if ((*a)[5] == ':') {
                id = *a + 6;
            } else if ((*a)[5] != '\0') {
                continue;
            }

            const std::string reference(*(a + 1));
            parser->startNamespace(id.c_str(), reference.c_str());
        }
    }

    parser->startElement(name, attributes);
}

// 标签数据回调
static void characterDataHandler(void *userData, const char *text, int len) {
    SAXParserImpl *parser = (SAXParserImpl *) userData;
    parser->characterData(text, len);
}

// 结尾标签回调
static void endElementHandler(void *userData, const char *name) {
    SAXParserImpl *parser = (SAXParserImpl *) userData;
    parser->endElement(name);
}

static void startNamespaceHandler(
        void *userData,
        const XML_Char *prefix,
        const XML_Char *uri) {
    SAXParserImpl *parser = (SAXParserImpl *) userData;
    parser->startNamespace(prefix, uri);
}


static void endNamespaceHandler(
        void *userData,
        const XML_Char *prefix) {
    SAXParserImpl *parser = (SAXParserImpl *) userData;
    parser->endNamespace(prefix);
}

// 未知编码回调。expat 只支持 4 种编码类型：UTF-8, UTF-16, ISO-8859-1, US-ASCII.
static int unknownEncodingHandler(void *userData, const XML_Char *name, XML_Encoding *encoding) {
    // TODO:暂时不处理回调错误的问题
    return XML_STATUS_ERROR;
}

SAXParserImpl::SAXParserImpl() : mXmlParser(nullptr),
                                 mParseStream(nullptr),
                                 mParserHandler(nullptr),
                                 isInitialized(false) {
    mParseBuffer = new char[BUFFER_SIZE];
}

SAXParserImpl::~SAXParserImpl() {
    if (mXmlParser != nullptr) {
        XML_ParserFree(mXmlParser);
    }

    delete[]mParseBuffer;
}

void SAXParserImpl::initParser() {
    // TODO：encoding 默认传空，不知道会不会有问题

    //  如果已经初始化，则进行重置操作
    if (!isInitialized) {
        // 创建解析器，并传入编码类型
        mXmlParser = XML_ParserCreate(NULL);
        isInitialized = true;
    } else {
        XML_ParserReset(mXmlParser, 0);
    }

    // 将当前对象作为上下文传递
    XML_SetUserData(mXmlParser, this);
    // TODO:对命名空间的处理 (expat 库使用 XML_ParserCreateNS 会崩溃，暂时先不处理)
    // XML_SetNamespaceDeclHandler(mXmlParser, startNamespaceHandler, endNamespaceHandler);
    // 起始标记回调
    XML_SetElementHandler(mXmlParser, startElementHandler, endElementHandler);
    // 标记数据回调
    XML_SetCharacterDataHandler(mXmlParser, characterDataHandler);
    // 位置编码信息回调
    XML_SetUnknownEncodingHandler(mXmlParser, unknownEncodingHandler, 0);
}

void SAXParserImpl::startNamespace(const char *prefix, const char *uri) {
    if (mParserHandler->isInterrupt()) {
        return;
    }

    std::string prefixValue(prefix);
    std::string uriValue(uri);

    mParserHandler->startNamespace(prefixValue, uriValue);
}

void SAXParserImpl::endNamespace(const char *prefix) {
    if (mParserHandler->isInterrupt()) {
        return;
    }

    std::string prefixValue(prefix);
    mParserHandler->endNamespace(prefixValue);
}

void SAXParserImpl::startElement(const char *name, const char **attributes) {
    // 将 attributes 进行封装
    if (mParserHandler->isInterrupt()) {
        return;
    }
    // 分割线的位置
    const char *splitIndex = strchr(name, TEXT_SPLIT);

    std::string localName;
    std::string fullName(name);

    if (splitIndex != nullptr) {
        localName.assign(splitIndex + 1);
    } else {
        localName.assign(fullName);
    }

    // 将 attributes 进行封装
    Attributes attrs(attributes);

    // 发送元素
    mParserHandler->startElement(localName, fullName, attrs);
}

void SAXParserImpl::characterData(const char *text, int len) {
    // 将 attributes 进行封装
    if (mParserHandler->isInterrupt()) {
        return;
    }
    std::string data(text, 0, len);

    mParserHandler->characterData(data);
}

void SAXParserImpl::endElement(const char *name) {
    // 将 attributes 进行封装


    // 分割线的位置
    const char *splitIndex = strchr(name, TEXT_SPLIT);

    std::string localName;
    std::string fullName(name);

    if (splitIndex != nullptr) {
        localName.assign(splitIndex + 1);
    } else {
        localName.assign(fullName);
    }

    mParserHandler->endElement(localName, fullName);
}

void SAXParserImpl::parse(std::shared_ptr<InputStream> &is, SAXHandler &handler) {
    if (!is->open()) {
        return;
    }

    // 赋值当前解析单元
    mParseStream = is;
    mParserHandler = &handler;

    // 初始化解析器
    initParser();

    // 通知解析开始
    handler.startDocument();

    // 进行循环解析操作
    std::size_t length;
    do {
        length = is->read(mParseBuffer, BUFFER_SIZE);
        if (!parseBuffer(mParseBuffer, length)) {
            break;
        }

    } while ((length == BUFFER_SIZE) && !handler.isInterrupt());

    is->close();

    // 通知解析结束
    handler.endDocument();

    // 清空解析单元
    mParseStream = nullptr;
    mParserHandler = nullptr;

    // 清空缓存
    memset(mParseBuffer, '\0', BUFFER_SIZE);
}

bool SAXParserImpl::parseBuffer(const char *buffer, std::size_t len) {
    // 如果判断结果
    bool result = XML_Parse(mXmlParser, buffer, len, 0) != XML_STATUS_ERROR;

    // 如果发生错误，发送错误信息
    if (!result) {
        std::string err(XML_ErrorString(XML_GetErrorCode(mXmlParser)));
        mParserHandler->error(err);
    }

    return result;
}

bool SAXParserImpl::checkState() {
    // TODO:如果停止，是否要调用 XML_STOP() 呢，
    // 之后思考，参考文档 http://web.mit.edu/freebsd/head/contrib/expat/doc/reference.html#XML_StopParser

    return mParserHandler->isInterrupt();
}

void SAXParserImpl::reset() {
    // 暂时不处理
}
