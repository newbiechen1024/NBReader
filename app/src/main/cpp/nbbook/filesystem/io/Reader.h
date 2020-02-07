// author : newbiechen
// date : 2019-11-29 23:36
// description : 参考 java 的 Reader 写的读取器。(用于处理编码问题)
//

#ifndef NBREADER_READER_H
#define NBREADER_READER_H

#include <string>

class Reader {
public:
    virtual ~Reader() {
    }

    /**
     * 启动读取
     * @return
     */
    virtual bool open() = 0;

    /**
     * 将读取到的数据写入缓冲区
     * @param buffer
     * @param length
     * @return 读取数据的长度。
     *
     * TODO：与 InputStream 不同的是 returnSize != length 不表示 Reader 结束。判断是否真正结束需要
     * @see isFinish()
     *
     */
    virtual int read(char *buffer, size_t length) = 0;

    /**
     * 停止读取
     */
    virtual void close() = 0;

    /**
     * 是否读取完成
     */
    virtual bool isFinish() const = 0;
};


#endif //NBREADER_READER_H
