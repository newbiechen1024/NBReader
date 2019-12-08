// author : newbiechen
// date : 2019-12-07 16:47
// description : 
//

#include <filesystem/io/InputStreamReader.h>
#include <util/Logger.h>
#include <filesystem/File.h>
#include <filesystem/FileSystem.h>
#include "ChapterDetector.h"

// 先设置为 256 吧，如果内存占用过大，可以改小一点
static const size_t BUFFER_SIZE = 1024 * 256;

static const std::string TAG = "ChapterDetector";

ChapterDetector::ChapterDetector(const std::string &pattern) : mPattern(pattern) {
}

void ChapterDetector::detector(std::shared_ptr<InputStream> inputStream,
                               const std::string &charset) {
/*
     // TODO:应该选择 \n 后的 buffer 数据，防止 \n 被截取一半的情况。

    // 创建缓冲区
    char *buffer = new char[BUFFER_SIZE]();

    // 输入流读取器
    InputStreamReader isReader(inputStream, charset);

    CharsetConverter encodingConverter("utf-8", charset);

    int readSize = 0;
    // TODO：readSize 不为 buffer_size 并不表示读取完成，现在先这么写，走一个流程。InputStreamReader 代码没写好。
    // 是否读取数据成功
    while ((readSize = isReader.read(buffer, BUFFER_SIZE)) == BUFFER_SIZE) {
        // 得到的都是 utf-8 数据，交给解析器解析
        Matcher matcher = mPattern.match(buffer);

        // 查找下一段落
        while (matcher.find()) {
            size_t start = matcher.start();
            size_t end = matcher.end();

            // TODO：生成 string ???，获取到文本写入本地
            std::string value(buffer + start, buffer + end);

            // TODO：如果已知 InputStream 读取的长度就好了，这样只需要将 startBuffer + matcher start 的部分转码
            CharBuffer outBuffer(512);
            CharBuffer inBuffer(buffer, BUFFER_SIZE);
            CharsetConverter::ResultCode code = encodingConverter.convert(inBuffer,outBuffer);

            // 存储 outBuffer 的 position

            // 最终得到 chapter 在原始文本上的偏移位置

            // TODO:存储到 vector 中
        }
    }*/

    // 如果读取完成
}