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

#ifndef __XMLREADER_H__
#define __XMLREADER_H__

#include <string>
#include <vector>
#include <map>

class File;

class InputStream;

class AsynchronousInputStream;

class XMLReaderInternal;

class XMLReader {

public:
    class NamePredicate {

    public:
        virtual ~NamePredicate();

        virtual bool accepts(const XMLReader &reader, const char *name) const = 0;

        virtual bool accepts(const XMLReader &reader, const std::string &name) const = 0;
    };

    class SimpleNamePredicate : public NamePredicate {

    public:
        SimpleNamePredicate(const std::string &name);

        bool accepts(const XMLReader &reader, const char *name) const;

        bool accepts(const XMLReader &reader, const std::string &name) const;

    private:
        const std::string myName;
    };

    class IgnoreCaseNamePredicate : public NamePredicate {

    public:
        IgnoreCaseNamePredicate(const std::string &lowerCaseName);

        bool accepts(const XMLReader &reader, const char *name) const;

        bool accepts(const XMLReader &reader, const std::string &name) const;

    private:
        const std::string myLowerCaseName;
    };

    class FullNamePredicate : public NamePredicate {

    public:
        FullNamePredicate(const std::string &ns, const std::string &name);

        bool accepts(const XMLReader &reader, const char *name) const;

        bool accepts(const XMLReader &reader, const std::string &name) const;

    private:
        const std::string myNamespaceName;
        const std::string myName;
    };

    class BrokenNamePredicate : public NamePredicate {

    public:
        BrokenNamePredicate(const std::string &name);

        bool accepts(const XMLReader &reader, const char *name) const;

        bool accepts(const XMLReader &reader, const std::string &name) const;

    private:
        const std::string myName;
    };

protected:
    XMLReader(const char *encoding = 0);

public:
    virtual ~XMLReader();

    bool readDocument(const File &file);

    bool readDocument(std::shared_ptr<InputStream> stream);

    bool readDocument(std::shared_ptr<AsynchronousInputStream> stream);

    const std::string &errorMessage() const;

    typedef std::map<std::string, std::string> nsMap;

    const nsMap &namespaces() const;

    const char *attributeValue(const char **xmlattributes, const char *name) const;

    const char *attributeValue(const char **xmlattributes, const NamePredicate &predicate) const;

    std::map<std::string, std::string> attributeMap(const char **xmlattributes) const;

private:
    void initialize(const char *encoding = 0);

    void shutdown();

    bool readFromBuffer(const char *data, std::size_t len);

protected:
    virtual void startElementHandler(const char *tag, const char **attributes);

    virtual void endElementHandler(const char *tag);

    virtual void characterDataHandler(const char *text, std::size_t len);

    virtual bool processNamespaces() const;

    virtual const std::vector<std::string> &externalDTDs() const;

    virtual void collectExternalEntities(std::map<std::string, std::string> &entityMap);

    bool testTag(const std::string &ns, const std::string &name, const std::string &tag) const;

    bool isInterrupted() const;

    std::size_t getCurrentPosition() const;

protected:
    void interrupt();

    void setErrorMessage(const std::string &message);

private:
    bool myInterrupted;
    XMLReaderInternal *myInternalReader;
    char *myParserBuffer;
    std::vector<std::shared_ptr<nsMap> > myNamespaces;

    std::string myErrorMessage;

    friend class XMLReaderInternal;

    friend class XMLReaderHandler;
};

inline bool XMLReader::isInterrupted() const {
    return myInterrupted;
}

inline void XMLReader::interrupt() {
    myInterrupted = true;
}

#endif /* __XMLREADER_H__ */
