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

#include <filesystem/File.h>
#include <filesystem/io/InputStream.h>
#include <util/StringUtil.h>
#include <util/UnicodeUtil.h>

#include "AsynchronousInputStream.h"

#include "XMLReader.h"
#include "XMLReaderInternal.h"

class XMLReaderHandler : public AsynchronousInputStream::Handler {

public:
    XMLReaderHandler(XMLReader &reader);

    void initialize(const char *encoding);

    void shutdown();

    bool handleBuffer(const char *data, std::size_t len);

private:
    XMLReader &myReader;
};

XMLReaderHandler::XMLReaderHandler(XMLReader &reader) : myReader(reader) {
}

void XMLReaderHandler::initialize(const char *encoding) {
    myReader.initialize(encoding);
}

void XMLReaderHandler::shutdown() {
    myReader.shutdown();
}

bool XMLReaderHandler::handleBuffer(const char *data, std::size_t len) {
    return myReader.readFromBuffer(data, len);
}

static const std::size_t BUFFER_SIZE = 2048;

void XMLReader::startElementHandler(const char *, const char **) {
}

void XMLReader::endElementHandler(const char *) {
}

void XMLReader::characterDataHandler(const char *, std::size_t) {
}

const XMLReader::nsMap &XMLReader::namespaces() const {
    return *myNamespaces.back();
}

XMLReader::XMLReader(const char *encoding) {
    myInternalReader = new XMLReaderInternal(*this, encoding);
    myParserBuffer = new char[BUFFER_SIZE];
}

XMLReader::~XMLReader() {
    delete[] myParserBuffer;
    delete myInternalReader;
}

bool XMLReader::readDocument(const File &file) {
    return readDocument(file.getInputStream());
}

bool XMLReader::readDocument(std::shared_ptr<InputStream> stream) {
    if (stream == nullptr || !stream->open()) {
        return false;
    }

    bool useWindows1252 = false;
    stream->read(myParserBuffer, 256);
    std::string stringBuffer(myParserBuffer, 256);
    stream->seek(0, true);
    int index = stringBuffer.find('>');
    if (index > 0) {
        stringBuffer = stringBuffer.substr(0, index);
        if (!UnicodeUtil::isUtf8String(stringBuffer)) {
            return false;
        }
        stringBuffer = UnicodeUtil::toLower(stringBuffer);
        int index = stringBuffer.find("\"iso-8859-1\"");
        if (index > 0) {
            useWindows1252 = true;
        }
    }

    initialize(useWindows1252 ? "windows-1252" : 0);

    std::size_t length;
    do {
        length = stream->read(myParserBuffer, BUFFER_SIZE);
        if (!readFromBuffer(myParserBuffer, length)) {
            break;
        }
    } while ((length == BUFFER_SIZE) && !myInterrupted);

    stream->close();

    shutdown();

    return true;
}

void XMLReader::initialize(const char *encoding) {
    myInternalReader->init(encoding);
    myInterrupted = false;
    myNamespaces.push_back(std::make_shared<nsMap>());
}

void XMLReader::shutdown() {
    myNamespaces.clear();
}

bool XMLReader::readFromBuffer(const char *data, std::size_t len) {
    return myInternalReader->parseBuffer(data, len);
}

bool XMLReader::processNamespaces() const {
    return false;
}

const std::vector<std::string> &XMLReader::externalDTDs() const {
    static const std::vector<std::string> EMPTY_VECTOR;
    return EMPTY_VECTOR;
}

void XMLReader::collectExternalEntities(std::map<std::string, std::string> &entityMap) {
}

const char *XMLReader::attributeValue(const char **xmlattributes, const char *name) const {
    while (*xmlattributes != 0) {
        bool useNext = std::strcmp(*xmlattributes, name) == 0;
        ++xmlattributes;
        if (*xmlattributes == 0) {
            return 0;
        }
        if (useNext) {
            return *xmlattributes;
        }
        ++xmlattributes;
    }
    return 0;
}

std::map<std::string, std::string> XMLReader::attributeMap(const char **xmlattributes) const {
    std::map<std::string, std::string> map;
    while (*xmlattributes != 0) {
        std::string key = *xmlattributes;
        ++xmlattributes;
        if (*xmlattributes == 0) {
            break;
        }
        map[key] = *xmlattributes;
        ++xmlattributes;
    }
    return map;
}

XMLReader::NamePredicate::~NamePredicate() {
}

XMLReader::SimpleNamePredicate::SimpleNamePredicate(const std::string &name) : myName(name) {
}

bool XMLReader::SimpleNamePredicate::accepts(const XMLReader &, const char *name) const {
    return myName == name;
}

bool XMLReader::SimpleNamePredicate::accepts(const XMLReader &, const std::string &name) const {
    return myName == name;
}

XMLReader::IgnoreCaseNamePredicate::IgnoreCaseNamePredicate(const std::string &lowerCaseName)
        : myLowerCaseName(lowerCaseName) {
}

bool
XMLReader::IgnoreCaseNamePredicate::accepts(const XMLReader &reader, const char *name) const {
    std::string lc = name;
    StringUtil::asciiToLowerInline(lc);
    return myLowerCaseName == lc;
}

bool
XMLReader::IgnoreCaseNamePredicate::accepts(const XMLReader &, const std::string &name) const {
    std::string lc = name;
    StringUtil::asciiToLowerInline(lc);
    return myLowerCaseName == lc;
}

XMLReader::FullNamePredicate::FullNamePredicate(const std::string &ns, const std::string &name)
        : myNamespaceName(ns), myName(name) {
}

bool XMLReader::FullNamePredicate::accepts(const XMLReader &reader, const char *name) const {
    return accepts(reader, std::string(name));
}

bool
XMLReader::FullNamePredicate::accepts(const XMLReader &reader, const std::string &name) const {
    const std::size_t index = name.find(':');
    const std::string namespaceId =
            index == std::string::npos ? std::string() : name.substr(0, index);

    const nsMap &namespaces = reader.namespaces();
    nsMap::const_iterator it = namespaces.find(namespaceId);
    return
            it != namespaces.end() &&
            it->second == myNamespaceName &&
            name.substr(index + 1) == myName;
}

XMLReader::BrokenNamePredicate::BrokenNamePredicate(const std::string &name) : myName(name) {
}

bool XMLReader::BrokenNamePredicate::accepts(const XMLReader &reader, const char *name) const {
    return accepts(reader, std::string(name));
}

bool XMLReader::BrokenNamePredicate::accepts(const XMLReader &reader,
                                             const std::string &name) const {
    return myName == name.substr(name.find(':') + 1);
}

const char *
XMLReader::attributeValue(const char **xmlattributes, const NamePredicate &predicate) const {
    while (*xmlattributes != 0) {
        bool useNext = predicate.accepts(*this, *xmlattributes);
        ++xmlattributes;
        if (*xmlattributes == 0) {
            return 0;
        }
        if (useNext) {
            return *xmlattributes;
        }
        ++xmlattributes;
    }
    return 0;
}

bool
XMLReader::testTag(const std::string &ns, const std::string &name, const std::string &tag) const {
    const nsMap &nspaces = namespaces();

    if (name == tag) {
        const nsMap::const_iterator it = nspaces.find(std::string());
        return it != nspaces.end() && ns == it->second;
    }
    const int nameLen = name.size();
    const int tagLen = tag.size();
    if (tagLen < nameLen + 2) {
        return false;
    }
    if (StringUtil::endsWith(tag, name) && tag[tagLen - nameLen - 1] == ':') {
        const nsMap::const_iterator it = nspaces.find(tag.substr(0, tagLen - nameLen - 1));
        return it != nspaces.end() && ns == it->second;
    }
    return false;
}

bool XMLReader::readDocument(std::shared_ptr<AsynchronousInputStream> stream) {
    XMLReaderHandler handler(*this);
    return stream->processInput(handler);
}

const std::string &XMLReader::errorMessage() const {
    return myErrorMessage;
}

void XMLReader::setErrorMessage(const std::string &message) {
    myErrorMessage = message;
    interrupt();
}

std::size_t XMLReader::getCurrentPosition() const {
    return myInternalReader != 0 ? myInternalReader->getCurrentPosition() : (std::size_t) -1;
}
