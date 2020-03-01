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

#ifndef __STYLESHEETTABLE_H__
#define __STYLESHEETTABLE_H__

#include <string>
#include <map>
#include <vector>

#include "CSSSelector.h"
#include "../../util/Boolean.h"
#include "../../reader/text/tag/StyleTag.h"

class StyleSheetTable {

public:
    typedef std::map<std::string, std::string> AttributeMap;

    static std::shared_ptr<StyleTag>
    createOrUpdateControl(const AttributeMap &map, std::shared_ptr<StyleTag> entry = 0);

private:
    void addMap(std::shared_ptr<CSSSelector> selector, const AttributeMap &map);

    static void setLength(StyleTag &entry, TextFeature featureId,
                          const AttributeMap &map, const std::string &attributeName);

    static const std::string &value(const AttributeMap &map, const std::string &name);

public:
    bool isEmpty() const;

    Boolean doBreakBefore(const std::string &tag, const std::string &aClass) const;

    Boolean doBreakAfter(const std::string &tag, const std::string &aClass) const;

    std::shared_ptr<StyleTag>
    control(const std::string &tag, const std::string &aClass) const;

    std::vector<std::pair<CSSSelector, std::shared_ptr<StyleTag>>>
    allControls(const std::string &tag, const std::string &aClass) const;

    void clear();

private:
    std::map<CSSSelector, std::shared_ptr<StyleTag> > myControlMap;
    std::map<CSSSelector, bool> myPageBreakBeforeMap;
    std::map<CSSSelector, bool> myPageBreakAfterMap;

    friend class StyleSheetTableParser;

    friend class StyleSheetParserWithCache;
};

#endif /* __STYLESHEETTABLE_H__ */
