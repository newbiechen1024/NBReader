// author : newbiechen
// date : 2019-09-24 13:29
// description : 
//

#ifndef NBREADER_FILEINPUTSTREAM_H
#define NBREADER_FILEINPUTSTREAM_H


#include "InputStream.h"
#include <string>
#include <filesystem/File.h>

class FileInputStream : InputStream {
public:
    FileInputStream(const std::string &filePath);

    FileInputStream(const File &file);

    ~FileInputStream();

    // 打开输入流
    bool open();

    // 读取数据
    size_t read(char *buffer, size_t maxSize);

    // 跳到具体位置
    void seek(int offset, bool absoluteOffset);

    // 当前位置
    size_t offset() const;

    // 关闭输入流
    void close();

private:
    std::string mFilePath;
    FILE *mFilePtr;
};


#endif //NBREADER_FILEINPUTSTREAM_H
