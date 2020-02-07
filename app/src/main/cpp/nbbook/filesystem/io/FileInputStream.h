// author : newbiechen
// date : 2019-09-24 13:29
// description : 
//

#ifndef NBREADER_FILEINPUTSTREAM_H
#define NBREADER_FILEINPUTSTREAM_H


#include "InputStream.h"
#include <string>
#include "../File.h"

class FileInputStream : public InputStream {
public:
    FileInputStream(const std::string &filePath);

    FileInputStream(const File &file);

    ~FileInputStream();

    // 打开输入流
    bool open() override;

    // 读取数据
    size_t read(char *buffer, size_t maxSize) override;

    // 跳到具体位置
    void seek(int offset, bool absoluteOffset) override;

    // 当前位置
    size_t offset() const override;

    // 关闭输入流
    void close() override;

private:
    std::string mFilePath;
    FILE *mFilePtr;
};


#endif //NBREADER_FILEINPUTSTREAM_H
