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


#include "FileEncryptionInfo.h"
#include "../../filesystem/File.h"

const std::string EncryptionMethod::UNSUPPORTED = "unsupported";
const std::string EncryptionMethod::EMBEDDING = "embedding";
const std::string EncryptionMethod::MARLIN = "marlin";
const std::string EncryptionMethod::KINDLE = "kindle";

FileEncryptionInfo::FileEncryptionInfo(const std::string &uri, const std::string &method,
                                       const std::string &algorithm, const std::string &contentId)
        : Uri(uri), Method(method), Algorithm(algorithm), ContentId(contentId) {
}

void EncryptionMap::addInfo(const FileDir &dir, std::shared_ptr<FileEncryptionInfo> info) {
    myPathToInfo[File(dir.fileNameToPath(info->Uri)).getPath()] = info;
}

std::shared_ptr<FileEncryptionInfo> EncryptionMap::info(const std::string &path) const {
    const std::map<std::string, std::shared_ptr<FileEncryptionInfo> >::const_iterator it = myPathToInfo.find(
            path);
    return it != myPathToInfo.end() ? it->second : 0;
}
