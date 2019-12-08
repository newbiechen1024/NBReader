// author : newbiechen
// date : 2019-11-29 23:45
// description : 文件流编码解析器,默认解析为 UTF-8
//

#ifndef NBREADER_STREAMDECODER_H
#define NBREADER_STREAMDECODER_H


#include <filesystem/io/InputStream.h>
#include <string>
#include <memory>
#include <iconv/iconv.h>
#include "../io/Reader.h"
#include "CharsetConverter.h"

// 默认转码成 UTF-8
class StreamDecoder : public Reader {
public:
    StreamDecoder(std::shared_ptr<InputStream> inputStream, const std::string &fromEncoding);

    ~StreamDecoder();

    bool open() override;

    /**
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     *
     * 返回值 > 0 表示解析的长度
     * 返回值 == 0 表示解析到末尾
     * 返回值 < 0 表示读取错误
     *
     * 错误码详见 errno 值。
     *
     */
    int read(char *buffer, size_t length) override;

    void close() override;

    bool isFinish() const override;

private:
    /**
     * 读取流数据
     * @return 是否读取到数据
     */
    bool readStream();

private:
    std::shared_ptr<InputStream> mInputStream;
    // 编码转换器
    CharsetConverter mDecoder;
    // 输入数据的缓冲区
    CharBuffer mInBuffer;
    // 判断是否解析完成
    bool isDecodeFinish;
};


#endif //NBREADER_STREAMDECODER_H
