//
// Created by 陈广祥 on 2019-09-18.
//

#include <plugin/txt/TxtPlugin.h>
#include <util/StringUtil.h>
#include "PluginManager.h"

// 初始化
std::vector<const std::string> PluginManager::sFormatTypeList = {
        FormatType::TXT,
        FormatType::EPUB
};

std::shared_ptr<FormatPlugin> PluginManager::createFormatPlugin(const std::string &type) const {
    std::string lowFormat = type;
    StringUtil::asciiToLowerInline(lowFormat);

    // 如果没有获取到类型，默认使用 TxtPlugin
    return std::make_shared<TxtPlugin>();
}

void PluginManager::readSupportFormat(std::vector<const std::string> &formats) const {
    // 返回支持的类型
    for (auto formatType: sFormatTypeList) {
        formats.push_back(formatType);
    }
}

bool PluginManager::isSupportFormat(const std::string &format) const {
    std::string lowFormat = format;
    StringUtil::asciiToLowerInline(lowFormat);

    for (auto formatType: sFormatTypeList) {
        if (formatType == lowFormat) {
            return true;
        }
    }
    return false;
}