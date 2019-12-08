// author : newbiechen
// date : 2019-11-29 23:39
// description : 
//

#ifndef NBREADER_INPUTSTREAMREADER_H
#define NBREADER_INPUTSTREAMREADER_H


#include <memory>
#include <string>
#include <iconv/iconv.h>
#include <filesystem/charset/StreamDecoder.h>
#include "Reader.h"
#include "InputStream.h"

class InputStreamReader : public Reader {

public:
    InputStreamReader(std::shared_ptr<InputStream> inputStream, const std::string &charset);

    ~InputStreamReader();

    void close() override;

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

    // 需要加一个方法判断是否 read 完成

    // 需要加一个方法

    bool open() override;

    bool isFinish() const override;

private:
    StreamDecoder mStreamDecoder;
};


#endif //NBREADER_INPUTSTREAMREADER_H
