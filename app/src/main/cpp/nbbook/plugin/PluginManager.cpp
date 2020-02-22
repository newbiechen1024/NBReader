//
// Created by 陈广祥 on 2019-09-18.
//

#include "PluginManager.h"
#include "../util/StringUtil.h"
#include "txt/TxtPlugin.h"
#include "oeb/OebPlugin.h"

// 初始化
std::vector<const std::string> PluginManager::sFormatTypeList = {
        FormatType::TXT, FormatType::EPUB
};

std::shared_ptr<FormatPlugin> PluginManager::createFormatPlugin(const std::string &type) {
    std::string lowFormat = type;
    StringUtil::asciiToLowerInline(lowFormat);

    if (type == FormatType::EPUB) {
        return std::make_shared<OebPlugin>();
    } else {
        // 如果没有获取到类型，默认使用 TxtPlugin
        return std::make_shared<TxtPlugin>();
    }
}

void PluginManager::readSupportFormat(std::vector<const std::string> &formats) {
    // 返回支持的类型
    for (auto formatType: sFormatTypeList) {
        formats.push_back(formatType);
    }
}

bool PluginManager::isSupportFormat(const std::string &format) {
    std::string lowFormat = format;
    StringUtil::asciiToLowerInline(lowFormat);

    for (auto formatType: sFormatTypeList) {
        if (formatType == lowFormat) {
            return true;
        }
    }
    return false;
}