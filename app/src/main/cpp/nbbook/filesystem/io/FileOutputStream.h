// author : newbiechen
// date : 2019-09-27 17:08
// description : 
//

#ifndef NBREADER_FILEOUTPUTSTREAM_H
#define NBREADER_FILEOUTPUTSTREAM_H


#include "OutputStream.h"
#include <string>

class File;

class FileOutputStream : public OutputStream {
public:
    FileOutputStream(const std::string &filePath);

    FileOutputStream(const File &file);

    ~FileOutputStream();

    bool open() override;

    /**
     * 是否允许
     * @param append
     * @return
     */
    bool open(bool append);

    void write(const char *data, size_t len) override;

    void write(const std::string &str) override;

    void close() override;

private:
    bool openFileInternal(bool append);

    std::string mPath;
    FILE *mFile;
};


#endif //NBREADER_FILEOUTPUTSTREAM_H
