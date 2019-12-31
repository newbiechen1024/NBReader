// author : newbiechen
// date : 2019-09-24 14:51
// description : 
//

#include <tools/encoding/EncodingConverterManager.h>
#include "EncodingTextReader.h"
#include <locale>

EncodingTextReader::EncodingTextReader(const std::string &charset) : mConverter(charset, "utf-8") {
}

void EncodingTextReader::convert(std::string &dst, const char *srcStart, const char *srcEnd) {
    size_t bufferSize = srcEnd - srcStart;
    if (bufferSize <= 0) {
        return;
    }
    // 强制转换成 char 应该没事吧，保证正确使用就行了
    CharBuffer inBuffer((char *) srcStart, bufferSize, bufferSize);
    CharBuffer outBuffer(bufferSize);

    // 进行循环调用
    mConverter.convert(inBuffer, outBuffer);

    CharsetConverter::ResultCode code;

    for (;;) {
        code = mConverter.convert(inBuffer, outBuffer);
        switch (code) {
            case CharsetConverter::OVERFLOW:
                dst.append(outBuffer.buffer(), outBuffer.position());
                // 清空 outBuffer 中的数据
                outBuffer.clear();
                // 新建一个 inBuffer，用于进行下次转换操作
                char *newStart = inBuffer.buffer() + inBuffer.position();
                size_t newSize = inBuffer.limit() - inBuffer.position();

                inBuffer = CharBuffer(newStart, newSize, newSize);
                break;
            case CharsetConverter::SUCCESS:
                dst.append(outBuffer.buffer(), outBuffer.position());
                break;
            default:
                // TODO:抛出异常
                exit(-1);
        }

        // 如果成功则退出循环
        if (code == CharsetConverter::SUCCESS) {
            break;
        }
    }
}