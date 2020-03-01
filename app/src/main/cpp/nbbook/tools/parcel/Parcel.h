// author : newbiechen
// date : 2020/3/1 7:37 PM
// description : 
//

#ifndef NBREADER_PARCEL_H
#define NBREADER_PARCEL_H

#include <cstdint>
#include <string>
#include "ParcelBuffer.h"

class Parcel {
public:
    Parcel(ParcelBuffer *buffer);

    ~Parcel() {
    }

    void writeBool(bool value);

    void writeInt8(int8_t value);

    /**
     * 写入 int_16 数据
     * @param value
     */
    void writeInt16(int16_t value);

    void writeInt32(int32_t value);

    void writeString16(const std::string &value);

    void writeString32(const std::string &value);

    void writeString16Array(const std::vector<std::string> &valueArr);

    void writeString32Array(const std::vector<std::string> &valueArr);

private:

    // 请求分配缓冲
    char *requestBuffer(size_t len);

    void writeStringInternal(const std::string &value);

private:
    // 缓冲分配器
    ParcelBuffer *mBuffer;
};


class Parcelable {
public:
    virtual ~Parcelable() {
    }

    virtual void writeToParcel(Parcel &parcel) = 0;
};

#endif //NBREADER_PARCEL_H
