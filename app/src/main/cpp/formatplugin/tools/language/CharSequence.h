// author : newbiechen
// date : 2019-10-18 15:24
// description : 可用的连续字节。由于编码方式的不同，不同的字节数才能合成一个字。
// 如：UTF-8 类型，必须使用 1~4 字节，才能表示一个字。

#ifndef NBREADER_CHARSEQUENCE_H
#define NBREADER_CHARSEQUENCE_H

#include <string>

class CharSequence {
public:
    CharSequence();

    CharSequence(const char *ptr, size_t size);

    CharSequence(const std::string &hexSequence);

    CharSequence(const CharSequence &other);

    ~CharSequence();

    CharSequence &operator=(const CharSequence &other);

    std::string toHexSequence() const;

    // returns
    //   an integer < 0 if the sequence is less than other
    //   an integer > 0 if the sequence is greater than other
    //   0 if the sequence is equal to other
    int compareTo(const CharSequence &other) const;

public:
    const char &operator[](size_t index) const {
        return mSequences[index];
    }

    size_t getSize() const {
        return mSize;
    }

private:
    // 连续字节数
    size_t mSize;
    // 起始字节指针
    char *mSequences;
};

// TODO：全局的 operator ??? 这种用法正确吗
inline bool operator<(const CharSequence &a, const CharSequence &b) {
    return a.compareTo(b) < 0;
}


#endif //NBREADER_CHARSEQUENCE_H
