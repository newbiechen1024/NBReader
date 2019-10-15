// author : newbiechen
// date : 2019-09-24 11:12
// description : 输出流抽象类
//

#ifndef NBREADER_OUTPUTSTREAM_H
#define NBREADER_OUTPUTSTREAM_H

#include <stddef.h>
#include <string>

class OutputStream {
public:
    virtual ~OutputStream();
    virtual bool open() = 0;
    virtual void write(const char *data, size_t len) = 0;
    virtual void write(const std::string &str) = 0;
    virtual void close() = 0;
protected:
    OutputStream();

/*private:
    OutputStream(const OutputStream&);
    const OutputStream &operator = (const OutputStream&);*/
};

inline OutputStream::OutputStream() {}
inline OutputStream::~OutputStream() {}

#endif //NBREADER_OUTPUTSTREAM_H
