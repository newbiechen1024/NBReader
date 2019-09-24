// author : newbiechen
// date : 2019-09-24 10:57
// description : 输入流抽象类
//

#ifndef NBREADER_INPUTSTREAM_H
#define NBREADER_INPUTSTREAM_H

#include <string>

class InputStream {

public:
    virtual ~InputStream();

    // 打开输入流
    virtual bool open() = 0;

    // 读取数据
    virtual std::size_t read(char *buffer, std::size_t maxSize) = 0;

    // 跳到具体位置
    virtual void seek(int offset, bool absoluteOffset) = 0;

    // 当前位置
    virtual std::size_t offset() const = 0;

    // 关闭输入流
    virtual void close() = 0;

protected:
    InputStream();

private:
    InputStream(const InputStream & is);
    const InputStream &operator=(const InputStream &is);
};

inline InputStream::InputStream() {
}

inline InputStream::~InputStream() {
}

#endif //NBREADER_INPUTSTREAM_H
