// author : newbiechen
// date : 2019-09-24 14:51
// description : 处理编码问题的 Reader
//

#ifndef NBREADER_ENCODINGTEXTREADER_H
#define NBREADER_ENCODINGTEXTREADER_H

#include <string>
#include <tools/encoding/Charset.h>
#include <filesystem/charset/CharsetConverter.h>

class EncodingTextReader {

public:
    // 进行转码操作
    void convert(std::string &dst, const char *srcStart, const char *srcEnd);

protected:
    // 传入编码类型
    EncodingTextReader(const std::string &charset);

    virtual ~EncodingTextReader() {
    }

private:
    // 编码转换器
    CharsetConverter mConverter;
};


#endif //NBREADER_ENCODINGTEXTREADER_H
