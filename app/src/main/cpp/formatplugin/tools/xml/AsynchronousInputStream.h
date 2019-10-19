/*
 * Copyright (C) 2009-2015 FBReader.ORG Limited <contact@fbreader.org>
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

#ifndef __ASYNCHRONOUSINPUTSTREAM_H__
#define __ASYNCHRONOUSINPUTSTREAM_H__

#include <string>


class AsynchronousInputStream {

public:
    class Handler {

    public:
        virtual ~Handler();

        virtual void initialize(const char *encoding) = 0;

        virtual void shutdown() = 0;

        virtual bool handleBuffer(const char *data, std::size_t len) = 0;
    };

public:
    AsynchronousInputStream(const char *encoding = 0);

    virtual ~AsynchronousInputStream();

    void setEof();

    void setBuffer(const char *data, std::size_t len);

    bool eof() const;

    bool initialized() const;

    bool processInput(Handler &handler);

protected:
    virtual bool processInputInternal(Handler &handler) = 0;

protected:
    const char *myData;
    std::size_t myDataLen;

private:
    std::string myEncoding;
    bool myInitialized;
    bool myEof;

private:
    // disable copying
    AsynchronousInputStream(const AsynchronousInputStream &);

    const AsynchronousInputStream &operator=(const AsynchronousInputStream &);
};

inline void AsynchronousInputStream::setEof() {
    myEof = true;
    myData = 0;
    myDataLen = 0;
}

inline void AsynchronousInputStream::setBuffer(const char *data, std::size_t len) {
    myData = data;
    myDataLen = len;
}

inline bool AsynchronousInputStream::eof() const { return myEof; }

inline bool AsynchronousInputStream::initialized() const { return myInitialized; }

#endif /* __ASYNCHRONOUSINPUTSTREAM_H__ */
