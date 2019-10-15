// author : newbiechen
// date : 2019-09-27 17:08
// description : 
//

#ifndef NBREADER_FILEOUTPUTSTREAM_H
#define NBREADER_FILEOUTPUTSTREAM_H


#include "OutputStream.h"
#include <string>
#include <filesystem/File.h>

class FileOutputStream : public OutputStream {
public:
    FileOutputStream(const std::string &filePath);

    FileOutputStream(const File &file);

    ~FileOutputStream();

    bool open() override;

    void write(const char *data, size_t len) override;

    void write(const std::string &str) override;

    void close() override;

private:
    std::string mTempPath;
    std::string mPath;
    FILE *mFile;

    bool hasError;
};


#endif //NBREADER_FILEOUTPUTSTREAM_H
