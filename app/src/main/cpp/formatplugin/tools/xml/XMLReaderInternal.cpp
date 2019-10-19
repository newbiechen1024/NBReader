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
#include <map>
#include <tools/encoding/EncodingConverter.h>
#include <filesystem/io/InputStream.h>
#include <tools/encoding/EncodingConverterManager.h>
#include <filesystem/File.h>
#include "XMLReaderInternal.h"
#include "XMLReader.h"

void XMLReaderInternal::fCharacterDataHandler(void *userData, const char *text, int len) {
    XMLReader &reader = *(XMLReader *) userData;
    if (!reader.isInterrupted()) {
        reader.characterDataHandler(text, len);
    }
}

void XMLReaderInternal::fStartElementHandler(void *userData, const char *name, const char **attributes) {
    XMLReader &reader = *(XMLReader *) userData;
    if (!reader.isInterrupted()) {
        if (reader.processNamespaces()) {
            int count = 0;
            for (const char **a = attributes; (*a != 0) && (*(a + 1) != 0); a += 2) {
                if (std::strncmp(*a, "xmlns", 5) == 0) {
                    std::string id;
                    if ((*a)[5] == ':') {
                        id = *a + 6;
                    } else if ((*a)[5] != '\0') {
                        continue;
                    }
                    if (count == 0) {
                        reader.myNamespaces.push_back(
                                std::make_shared<std::map<std::string, std::string>>(*reader.myNamespaces.back())
                        );
                    }
                    ++count;
                    const std::string reference(*(a + 1));
                    (*reader.myNamespaces.back())[id] = reference;
                }
            }
            if (count == 0) {
                reader.myNamespaces.push_back(reader.myNamespaces.back());
            }
        }
        reader.startElementHandler(name, attributes);
    }
}

void XMLReaderInternal::fEndElementHandler(void *userData, const char *name) {
    XMLReader &reader = *(XMLReader *) userData;
    if (!reader.isInterrupted()) {
        reader.endElementHandler(name);
        if (reader.processNamespaces()) {
            std::shared_ptr<std::map<std::string, std::string>> oldMap = reader.myNamespaces.back();
            reader.myNamespaces.pop_back();
        }
    }
}

static int fUnknownEncodingHandler(void *, const XML_Char *name, XML_Encoding *encoding) {

    std::shared_ptr<EncodingConverter> converter = EncodingConverterManager::getInstance()
            .getEncodingConverter(strToCharset(name));

    if (converter != nullptr) {

        for (int i = 0; i < 255; ++i) {
            encoding->map[i] = i;
        }

        return XML_STATUS_OK;
    }
    return XML_STATUS_ERROR;
}

static const std::size_t BUFSIZE = 2048;

static void parseDTD(XML_Parser parser, const std::string &fileName) {
    XML_Parser entityParser = XML_ExternalEntityParserCreate(parser, 0, 0);
    File dtdFile(fileName);
    std::shared_ptr<InputStream> entityStream = dtdFile.getInputStream();
    if (entityStream != nullptr && entityStream->open()) {
        char buffer[BUFSIZE];
        std::size_t length;
        do {
            length = entityStream->read(buffer, BUFSIZE);
            if (XML_Parse(entityParser, buffer, length, 0) == XML_STATUS_ERROR) {
                break;
            }
        } while (length == BUFSIZE);
    }
    XML_ParserFree(entityParser);
}

static void parseExtraDTDEntities(XML_Parser parser, const std::map<std::string, std::string> &entityMap) {
    XML_Parser entityParser = XML_ExternalEntityParserCreate(parser, 0, 0);
    std::string buffer;

    std::map<std::string, std::string>::const_iterator it = entityMap.begin();
    for (; it != entityMap.end(); ++it) {
        buffer.clear();
        buffer.append("<!ENTITY ").append(it->first).append(" \"").append(it->second).append("\">");
        if (XML_Parse(entityParser, buffer.data(), buffer.size(), 0) == XML_STATUS_ERROR) {
            break;
        }
    }
    XML_ParserFree(entityParser);
}

XMLReaderInternal::XMLReaderInternal(XMLReader &reader, const char *encoding) : myReader(reader) {
    myParser = XML_ParserCreate(encoding);
    myInitialized = false;
}

XMLReaderInternal::~XMLReaderInternal() {
    XML_ParserFree(myParser);
}

void XMLReaderInternal::setupEntities() {
    const std::vector<std::string> &dtds = myReader.externalDTDs();
    for (std::vector<std::string>::const_iterator it = dtds.begin(); it != dtds.end(); ++it) {
        myDTDStreamLocks.insert(File(*it).getInputStream());
        parseDTD(myParser, *it);
    }

    std::map<std::string, std::string> entityMap;
    myReader.collectExternalEntities(entityMap);
    if (!entityMap.empty()) {
        parseExtraDTDEntities(myParser, entityMap);
    }
}

void XMLReaderInternal::init(const char *encoding) {
    if (myInitialized) {
        XML_ParserReset(myParser, encoding);
    }

    myInitialized = true;
    XML_UseForeignDTD(myParser, XML_TRUE);

    setupEntities();

    XML_SetUserData(myParser, &myReader);
    if (encoding != 0) {
        XML_SetEncoding(myParser, encoding);
    }
    XML_SetStartElementHandler(myParser, fStartElementHandler);
    XML_SetEndElementHandler(myParser, fEndElementHandler);
    XML_SetCharacterDataHandler(myParser, fCharacterDataHandler);
    XML_SetUnknownEncodingHandler(myParser, fUnknownEncodingHandler, 0);
}

bool XMLReaderInternal::parseBuffer(const char *buffer, std::size_t len) {
    return XML_Parse(myParser, buffer, len, 0) != XML_STATUS_ERROR;
}

std::size_t XMLReaderInternal::getCurrentPosition() const {
    return XML_GetCurrentByteIndex(myParser);
}
