// author : newbiechen
// date : 2019-12-30 13:22
// description : 将文本格式转换成统一的 .nb 格式

#ifndef NBREADER_TEXTENCODER_H
#define NBREADER_TEXTENCODER_H

#include "../../tools/parcel/ParcelBuffer.h"
#include "entity/TextParagraph.h"
#include "tag/TextKind.h"
#include "tag/StyleTag.h"
#include "tag/ImageTag.h"
#include "tag/ContentTag.h"
#include <string>

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
    void beginParagraph(TextParagraph::Type paragraphType);

    /**
     * 添加文本信息
     */
    void addText(const std::vector<std::string> &text);

    /**
     * 添加文本标签
     * @param tag
     */
    void addTextTag(const TextTag &tag);

    size_t getCurParagraphCount() {
        return mCurParagraphCount;
    }

private:
    void checkEncoderState();

    void checkTagState();

    // 更新文本信息
    void updateText();

    // 结束段落
    void endParagraph();

    void release();

private:
    // 文本数据存储缓冲
    ParcelBuffer *mParcelBuffer;
    // 数据包对象
    Parcel *mParcel;
    // 当前创建的文本段落指针
    TextParagraph *mCurParagraphPtr;
    // 当前标签指针
    ContentTag *mContentTagPtr;
    // 当前段落总数
    size_t mCurParagraphCount;
    // 编码器是否打开
    bool mIsOpen;
};


#endif //NBREADER_TEXTENCODER_H
