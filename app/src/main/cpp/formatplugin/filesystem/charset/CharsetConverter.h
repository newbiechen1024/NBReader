// author : newbiechen
// date : 2019-11-28 20:08
// description :编码转换器
//

#ifndef NBREADER_CHARSETCONVERTER_H
#define NBREADER_CHARSETCONVERTER_H

#include <string>
#include <iconv/iconv.h>
#include <filesystem/buffer/CharBuffer.h>

class CharsetConverter {
public:
    //
    enum ResultCode {
        SUCCESS, // 成功
        OVERFLOW, // outBuffer 已经被填充满
        UNDERFLOW, // inBuffer 读取错误
        MALFORMED, // inBuffer 中的数据与 fromEncoding 不匹配
        UNKNOW // 未知错误
    };

    /**
     *
     * @param fromEncoding：当前文本编码
     * @param toEncoding：转换后的编码
     */
    CharsetConverter(const std::string &fromEncoding, const std::string &toEncoding);

    ~CharsetConverter();

    /**
     *
     * @param inBuffer
     * @param outBuffer
     * @return 返回的错误码
     * @see ErrorCode
     */
    ResultCode convert(CharBuffer &inBuffer, CharBuffer &outBuffer);

    std::string getFromEncoding() {
        return mFromEncoding;
    }

    std::string getToEncoding() {
        return mToEncoding;
    }

private:
    // iconv 的句柄
    libiconv_t mIconvCd;
    std::string mFromEncoding;
    std::string mToEncoding;
};


#endif //NBREADER_CHARSETCONVERTER_H
