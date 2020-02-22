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

#ifndef __NCXREADER_H__
#define __NCXREADER_H__

#include <map>
#include <vector>

#include "../../tools/xml/SAXHandler.h"

class NcxReader : public SAXHandler {

public:
    struct NavPoint {
        NavPoint();

        NavPoint(int order, std::size_t level);

        // 对应 playOrder 标签
        int Order;
        // 自定义的优先级，根据读取标签数自增 (暂时不知道作用，辅助 order 定位？)
        std::size_t Level;
        // 对应文本
        std::string Text;
        // 对应文本链接
        std::string ContentHRef;
    };

public:
    NcxReader();

    /**
     * .ncx 解析后的 order 与对应的 NavPoint 标签节点信息。
     * order 表示优先级
     * @return
     */
    const std::map<int, NavPoint> &navigationMap() const;

    void readFile(const File &ncxFile);

    void
    startElement(std::string &localName, std::string &fullName, Attributes &attributes) override;

    void characterData(std::string &data) override;

    void endElement(std::string &localName, std::string &fullName) override;


private:
    std::map<int, NavPoint> myNavigationMap;
    std::vector<NavPoint> myPointStack;

    enum {
        READ_NONE,
        READ_MAP,
        READ_POINT,
        READ_LABEL,
        READ_TEXT
    } myReadState;

    int myPlayIndex;
};

#endif /* __NCXREADER_H__ */
