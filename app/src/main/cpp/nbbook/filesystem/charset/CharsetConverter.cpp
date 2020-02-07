// author : newbiechen
// date : 2019-11-28 20:08
// description : 
//

#include "CharsetConverter.h"
#include "../../util/StringUtil.h"
#include "../../util/Logger.h"

static const std::string TAG = "CharsetConverter";

CharsetConverter::CharsetConverter(const std::string &fromEncoding, const std::string &toEncoding) {

    std::string lowFromEncoding = fromEncoding;
    StringUtil::asciiToLowerInline(lowFromEncoding);
    std::string lowToEncoding = toEncoding;
    StringUtil::asciiToLowerInline(lowToEncoding);

    // 不处理 charset 不支持，或者写错的情况
    mIconvCd = iconv_open(lowToEncoding.c_str(), lowFromEncoding.c_str());

    // 如果初始化失败，则 icon cd  为 null
    if (errno == EINVAL) {
        mIconvCd = nullptr;
        //TODO: 直接抛出异常，暂时为直接退出
        exit(-1);
    }
}

CharsetConverter::~CharsetConverter() {
    if (mIconvCd != nullptr) {
        iconv_close(mIconvCd);
        mIconvCd = nullptr;
    }
}

CharsetConverter::ResultCode
CharsetConverter::convert(CharBuffer &inBuffer, CharBuffer &outBuffer) {

    if (mIconvCd == nullptr) {
        // TODO:未处理，暂时直接返回错误
        Logger::i(TAG, "iconv init error");
        return UNKNOW;
    }

    // 获取输入缓冲区的数据长度
    size_t inBufferLen = inBuffer.position();
    // 剩余输出缓冲的长度
    size_t remainOutBufferLen = outBuffer.limit() - outBuffer.position();

    // 如果输入、输出都为 0，则不处理
    if (inBufferLen == 0 || remainOutBufferLen == 0) {
        return SUCCESS;
    }

    Logger::i(TAG, "inBufferLen = " + std::to_string(inBufferLen));

    // 输入缓冲指针
    char *inBufferPtr = inBuffer.buffer();

    // 输出缓冲指针
    char *outBufferPtr = outBuffer.buffer() + outBuffer.position();

    size_t resultInBufferLen = inBufferLen;
    size_t resultOutBufferLen = remainOutBufferLen;

    // 进行解析操作
    int resultCode = iconv(mIconvCd, &inBufferPtr, &resultInBufferLen, &outBufferPtr,
                           &resultOutBufferLen);

    Logger::i(TAG, "resultInBufferLen = " + std::to_string(resultInBufferLen));

    // 处理 inBuffer

    inBuffer.flip();
    // 指向已处理的区域
    inBuffer.position(inBufferLen - resultInBufferLen);

    // 修改 outBuffer 的 position
    outBuffer.position(outBuffer.position() + (remainOutBufferLen - resultOutBufferLen));

    ResultCode code = SUCCESS;

    // iconv 存在的错误类型只存在这 3 种。
    if (resultCode == -1) {
        switch (errno) {
            // out
            case E2BIG:
                code = OVERFLOW;
                break;
            case EINVAL:
                code = UNDERFLOW;
                break;
            case EILSEQ:
                code = MALFORMED;
                break;
        }
    }

    // 返回结果值
    return code;
}