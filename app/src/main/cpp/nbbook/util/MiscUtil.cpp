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

#include <cstdlib>

#include "MiscUtil.h"
#include "../reader/text/tag/TextKind.h"
#include "StringUtil.h"
#include "../filesystem/File.h"

TextKind MiscUtil::referenceType(const std::string &link) {
    std::string lowerCasedLink = link;
    const bool isFileReference =
            StringUtil::startsWith(lowerCasedLink, "http://") ||
            StringUtil::startsWith(lowerCasedLink, "https://") ||
            StringUtil::startsWith(lowerCasedLink, "ftp://");

    if (!isFileReference) {
        return
                StringUtil::startsWith(lowerCasedLink, "mailto:") ||
                StringUtil::startsWith(lowerCasedLink, "fbreader-action:") ||
                StringUtil::startsWith(lowerCasedLink, "com-fbreader-action:")
                ? TextKind::EXTERNAL_HYPERLINK : TextKind::INTERNAL_HYPERLINK;
    }
    return TextKind::EXTERNAL_HYPERLINK;
}

std::string MiscUtil::htmlDirectoryPrefix(const std::string &fileName) {
    File file(fileName);
    std::string shortName = file.getName();
    std::string path = file.getPath();
    int index = -1;
    if ((path.length() > shortName.length()) &&
        (path[path.length() - shortName.length() - 1] == ':')) {
        index = shortName.rfind('/');
    }
    return path.substr(0, path.length() - shortName.length() + index + 1);
}

std::string MiscUtil::htmlFileName(const std::string &fileName) {
    File file(fileName);
    std::string shortName = file.getName();
    std::string path = file.getPath();
    int index = -1;
    if ((path.length() > shortName.length()) &&
        (path[path.length() - shortName.length() - 1] == ':')) {
        index = shortName.rfind('/');
    }
    return path.substr(path.length() - shortName.length() + index + 1);
}

std::string MiscUtil::decodeHtmlURL(const std::string &encoded) {
    char buffer[3];
    buffer[2] = '\0';

    std::string decoded;
    const int len = encoded.length();
    decoded.reserve(len);
    for (int i = 0; i < len; i++) {
        if ((encoded[i] == '%') && (i < len - 2)) {
            buffer[0] = *(encoded.data() + i + 1);
            buffer[1] = *(encoded.data() + i + 2);
            decoded += (char) std::strtol(buffer, 0, 16);
            i += 2;
        } else {
            decoded += encoded[i];
        }
    }
    return decoded;
}
