// author : newbiechen
// date : 2019-10-18 15:24
// description : 
//

#include "CharSequence.h"

CharSequence::CharSequence() : mSize(0), mSequences(0) {
}

CharSequence::CharSequence(const char *ptr, std::size_t size) : mSize(size) {
    if (mSize == 0) {
        mSequences = 0;
        return;
    }
    mSequences = new char[mSize];
    for (std::size_t count = 0; count < mSize; ++count) {
        mSequences[count] = ptr[count];
    }
}

CharSequence::CharSequence(const CharSequence &other) : mSize(other.mSize) {
    if (mSize == 0) {
        mSequences = 0;
        return;
    }
    mSequences = new char[other.mSize];
    for (std::size_t count = 0; count < mSize; ++count) {
        mSequences[count] = other.mSequences[count];
    }
}

CharSequence::CharSequence(const std::string &hexSequence) {
    mSize = (hexSequence.size() + 1) / 5;
    mSequences = new char[mSize];
    for (std::size_t count = 0; count < mSize; ++count) {
        char a = hexSequence[count * 5 + 2];
        char b = hexSequence[count * 5 + 3];
        a -= (a >= 97) ? 87 : 48;
        b -= (b >= 97) ? 87 : 48;
        mSequences[count] = a * 16 + b;
    }
}

CharSequence::~CharSequence() {
    if (mSequences != nullptr) {
        delete[] mSequences;
    }
}

CharSequence &CharSequence::operator=(const CharSequence &other) {
    if (this != &other) {
        if (mSize != other.mSize && mSequences != 0) {
            delete[] mSequences;
            mSequences = 0;
        }
        mSize = other.mSize;
        if (other.mSequences != 0) {
            if (mSequences == 0) {
                mSequences = new char[mSize];
            }
            for (std::size_t count = 0; count < mSize; ++count) {
                mSequences[count] = other.mSequences[count];
            }
        }
    }
    return *this;
}

std::string CharSequence::toHexSequence() const {
    std::string result;
    static const char table[] = "0123456789abcdef";
    for (std::size_t count = 0; count < mSize; ++count) {
        result += "0x";
        result += table[(mSequences[count] >> 4) & 0x0F];
        result += table[mSequences[count] & 0x0F];
        if (count == mSize - 1) {
            return result;
        }
        result += " ";
    }
    return result;
}

int CharSequence::compareTo(const CharSequence &other) const {
    int difference = mSize - other.mSize;
    if (difference != 0) {
        return difference;
    }
    for (std::size_t i = 0; i < mSize; ++i) {
        int a = (int) (unsigned int) (unsigned char) mSequences[i];
        int b = (int) (unsigned int) (unsigned char) other.mSequences[i];
        difference = a - b;
        if (difference != 0) {
            return difference;
        }
    }
    return 0;
}