// author : newbiechen
// date : 2019-12-30 13:22
// description : 将文本格式转换成统一的 .nb 格式

#ifndef NBREADER_TEXTENCODER_H
#define NBREADER_TEXTENCODER_H

#include "TextBufferAllocator.h"
#include <string>
#include <reader/text/tag/TextStyleType.h>
#include <reader/text/entity/TextParagraph.h>

class TextEncoder {
public:
    TextEncoder();

    ~TextEncoder();

    /**
     * 打开编码器
     * @return
     */
    void open();

    /**
     * 关闭编码器
     * @param outBuffer
     * @return
     */
    size_t close(char **outBuffer);

    /**
     * 判断编码器是否开启
     * @return
     */
    bool isOpen() {
        return mIsOpen;
    }

    // 返回当前指针
    TextParagraph *getCurParagraph() {
        return mCurParagraphPtr;
    }

    /**
     * 根据段落类型创建段落
     * @param paragraphType：段落类型
     */
    void createParagraph(TextParagraph::Type paragraphType);

    /**
     * 添加文本标签信息
     */
    void addTextTag(const std::vector<std::string> &text);

    /**
     * 添加控制位标签信息
     * @param style:   NBReader 具有的文本样式类型
     * @param isStartTag：是否是起始标签
     */
    void addControlTag(TextStyleType style, bool isStartTag);

private:
    void checkEncoderState();

    void checkTagState();

    void flush();

    void release();

private:
    // 文本数据存储缓冲
    TextBufferAllocator *mBufferAllocatorPtr;
    // 当前创建的文本段落指针
    TextParagraph *mCurParagraphPtr;
    // 当前标签指针
    char *mCurTagPtr;
    // 编码器是否打开
    bool mIsOpen;
};


#endif //NBREADER_TEXTENCODER_H
