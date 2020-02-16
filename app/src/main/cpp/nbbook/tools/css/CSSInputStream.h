/*
 * Copyright (C) 2004-2015 FBReader.ORG Limited <contact@fbreader.org>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

#ifndef __CSSINPUTSTREAM_H__
#define __CSSINPUTSTREAM_H__

#include <memory>
#include "../../filesystem/io/InputStream.h"

class CSSInputStream : public InputStream {

public:
    CSSInputStream(std::shared_ptr<InputStream> base);

    ~CSSInputStream();

    bool open() override;

    size_t read(char *buffer, size_t maxSize) override;

    void seek(int offset, bool absoluteOffset) override;

    size_t offset() const override;

    size_t length() const override;

    void close() override;

protected:
    CSSInputStream();

private:
    void fillBufferNoComments();

private:
    enum State {
        PLAIN_TEXT,
        S_QUOTED_TEXT,
        D_QUOTED_TEXT,
        COMMENT_START_SLASH,
        COMMENT,
        COMMENT_END_ASTERISK
    };

    struct Buffer {
        Buffer(std::size_t capacity);

        ~Buffer();

        bool isEmpty() const;

        bool isFull() const;

        const std::size_t Capacity;
        std::size_t Offset;
        std::size_t Length;
        char *Content;
    };

    std::shared_ptr<InputStream> mBaseStream;

    Buffer mBuffer;
    Buffer mBufferNoComments;
    State mState;
};

inline bool CSSInputStream::Buffer::isEmpty() const {
    return Offset == Length;
}

inline bool CSSInputStream::Buffer::isFull() const {
    return Length >= Capacity;
}

#endif /* __CSSINPUTSTREAM_H__ */
