// author : newbiechen
// date : 2019-11-29 23:45
// description : 
//

#include "StreamDecoder.h"
#include "../../util/Logger.h"

static const std::string TAG = "StreamDecoder";
static const int BUFFER_SIZE = 1024 * 8;

StreamDecoder::StreamDecoder(std::shared_ptr<InputStream> inputStream,
                             const std::string &fromEncoding)
        : mInputStream(inputStream), mDecoder(fromEncoding, "utf-8"), mInBuffer(BUFFER_SIZE) {
    isDecodeFinish = false;
    mDecodeLength = 0;

    // TODO：为了解决 readStream 时的 compact()，虽然很怪异，但是没有什么其他好办法解决问题
    mInBuffer.position(BUFFER_SIZE);
}

StreamDecoder::~StreamDecoder() {
    close();
}

bool StreamDecoder::open() {
    return mInputStream->open();
}

int StreamDecoder::read(char *buffer, size_t length) {

    // 检测传入参数
    if (buffer == nullptr) {
        exit(-1);
    }

    if (length == 0) {
        return 0;
    }

    // 已经读取完成，就直接返回成功
    if (isFinish()) {
        return 0;
    }

    // 将 buffer 进行封装
    CharBuffer outBuffer(buffer, length);

    CharsetConverter::ResultCode resultCode;

    // 死循环
    for (;;) {
        // 开始读取数据
        if (!readStream()) {
            // 如果输入数据没有可解析的了，那么就直接返回
            isDecodeFinish = true;
            // 如果读取不到数据直接返回
            return outBuffer.position();
        }

        // 进行转码操作
        resultCode = mDecoder.convert(mInBuffer, outBuffer);
        // 记录解析数据的总长度
        mDecodeLength += mInBuffer.position();
        // 根据错误提示进行具体的处理
        switch (resultCode) {
            case CharsetConverter::OVERFLOW: // 说明 outBuffer 已经存满了
                Logger::i(TAG, "OVERFLOW");
                // 说明 buffer 填充成功，返回读取的长度
                return outBuffer.position();
            case CharsetConverter::UNDERFLOW: // 说明 inBuffer 中的数据不完整
                Logger::i(TAG, "UNDERFLOW");
                // TODO：如果不是从结尾导致的错误，而是中间的字符无法识别。该怎么处理
                // TODO：判断 position 如果离结尾 < 6 字节，可能是结尾导致的 UNDERFLOW
                // TODO：如果 position > 6 字节，可能是某个数据块无法识别，直接抛出异常？
                // TODO：暂时不处理
                break;
            case CharsetConverter::MALFORMED: // 说明编码格式无法解析
                Logger::i(TAG, "MALFORMED");
                // TODO:说明 inBuffer 数据与编码不符合，应该抛出异常(当前，暂时处理为解析结束)
                isDecodeFinish = true;
                return outBuffer.position();
            case CharsetConverter::SUCCESS: // 说明 inBuffer 完全解析完成。
                Logger::i(TAG, "SUCCESS");

                // 在完全解析完的情况下，判断 outBuffer 是否有剩余空间，如果没有剩余空间则直接返回。
                // 虽然这种判断也不准确，但是聊胜于无。
                if (outBuffer.position() == outBuffer.size()) {
                    return outBuffer.position();
                }
                break;
            default:
                isDecodeFinish = true;
                // 如果读取不到数据直接返回
                return outBuffer.position();
        }
    }
}

bool StreamDecoder::readStream() {
    // 重置缓冲区
    mInBuffer.compact();

    int pos = mInBuffer.position();

    char *readBuffer = mInBuffer.buffer() + pos;

    int remainSize = mInBuffer.remaining();

    // 如果剩余大小不为 0 才读取数据
    if (remainSize != 0) {
        int resultSize = mInputStream->read(readBuffer, remainSize);
        mInBuffer.position(pos + resultSize);
    }

    return mInBuffer.position() != 0;
}

void StreamDecoder::close() {
    mInputStream->close();
    isDecodeFinish = false;
    mDecodeLength = 0;
}

bool StreamDecoder::isFinish() const {
    return isDecodeFinish;
}