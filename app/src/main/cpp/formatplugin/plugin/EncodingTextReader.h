// author : newbiechen
// date : 2019-09-24 14:51
// description : 处理编码问题的 Reader
//

#ifndef NBREADER_ENCODINGTEXTREADER_H
#define NBREADER_ENCODINGTEXTREADER_H

#include <string>
#include <tools/encoding/EncodingConverter.h>
#include <tools/encoding/Charset.h>

class EncodingTextReader {

public:
    std::shared_ptr<EncodingConverter> getEncodingConverter() {
        return mConverter;
    }

protected:
    // 传入编码类型
    EncodingTextReader(const std::string & charset);

    virtual ~EncodingTextReader() {
    }

private:
    // 持有编码转换器
    std::shared_ptr<EncodingConverter> mConverter;
};


#endif //NBREADER_ENCODINGTEXTREADER_H
