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

#include <cstring>
#include <algorithm>
#include "CSSInputStream.h"
#include "../../filesystem/io/InputStream.h"

static const size_t BUFFER_SIZE = 8192;

CSSInputStream::Buffer::Buffer(std::size_t capacity) : Capacity(capacity - 1) {
    Content = new char[capacity];
    Length = 0;
    Offset = 0;
}

CSSInputStream::Buffer::~Buffer() {
    delete[] Content;
}

CSSInputStream::CSSInputStream(std::shared_ptr<InputStream> base) : mBaseStream(base),
                                                                    mBuffer(BUFFER_SIZE),
                                                                    mBufferNoComments(BUFFER_SIZE) {
}

CSSInputStream::~CSSInputStream() {
    close();
}

bool CSSInputStream::open() {
    mState = PLAIN_TEXT;
    return mBaseStream->open();
}

std::size_t CSSInputStream::read(char *buffer, std::size_t maxSize) {
    std::size_t ready = 0;
    while (ready < maxSize) {
        fillBufferNoComments();
        if (mBufferNoComments.isEmpty()) {
            break;
        }
        std::size_t len = std::min(
                maxSize - ready,
                mBufferNoComments.Length - mBufferNoComments.Offset
        );
        if (buffer != 0) {
            std::memcpy(buffer + ready, mBufferNoComments.Content + mBufferNoComments.Offset,
                        len);
        }
        mBufferNoComments.Offset += len;
        ready += len;
    }
    return ready;
}

void CSSInputStream::close() {
    return mBaseStream->close();
}

void CSSInputStream::seek(int offset, bool absoluteOffset) {
    // TODO: implement
}

std::size_t CSSInputStream::offset() const {
    // TODO: not a correct computation
    return mBaseStream->offset();
}

size_t CSSInputStream::length() const {
    return mBaseStream->length();
}

void CSSInputStream::fillBufferNoComments() {
    if (!mBufferNoComments.isEmpty()) {
        return;
    }
    mBufferNoComments.Length = 0;
    mBufferNoComments.Offset = 0;
    while (!mBufferNoComments.isFull()) {
        if (mBuffer.isEmpty()) {
            mBuffer.Offset = 0;
            mBuffer.Length = mBaseStream->read(mBuffer.Content, mBuffer.Capacity);
        }
        if (mBuffer.isEmpty()) {
            break;
        }
        while (!mBuffer.isEmpty() && !mBufferNoComments.isFull()) {
            const char ch = mBuffer.Content[mBuffer.Offset++];
            switch (mState) {
                case PLAIN_TEXT:
                    switch (ch) {
                        case '\'':
                            mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                            mState = S_QUOTED_TEXT;
                            break;
                        case '"':
                            mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                            mState = D_QUOTED_TEXT;
                            break;
                        case '/':
                            mState = COMMENT_START_SLASH;
                            break;
                        default:
                            mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                            break;
                    }
                    break;
                case S_QUOTED_TEXT:
                    if (ch == '\'') {
                        mState = PLAIN_TEXT;
                    }
                    mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                    break;
                case D_QUOTED_TEXT:
                    if (ch == '"') {
                        mState = PLAIN_TEXT;
                    }
                    mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                    break;
                case COMMENT_START_SLASH:
                    switch (ch) {
                        case '/':
                            mBufferNoComments.Content[mBufferNoComments.Length++] = '/';
                            break;
                        case '*':
                            mState = COMMENT;
                            break;
                        default:
                            mState = PLAIN_TEXT;
                            mBufferNoComments.Content[mBufferNoComments.Length++] = '/';
                            mBufferNoComments.Content[mBufferNoComments.Length++] = ch;
                            break;
                    }
                    break;
                case COMMENT:
                    if (ch == '*') {
                        mState = COMMENT_END_ASTERISK;
                    }
                    break;
                case COMMENT_END_ASTERISK:
                    switch (ch) {
                        case '/':
                            mState = PLAIN_TEXT;
                            break;
                        case '*':
                            break;
                        default:
                            mState = COMMENT;
                            break;
                    }
                    break;
            }
        }
    }
}
