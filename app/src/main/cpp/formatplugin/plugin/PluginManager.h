//
// Created by 陈广祥 on 2019-09-18.
//

#ifndef NBREADER_PLUGINMANAGER_H
#define NBREADER_PLUGINMANAGER_H

#include <vector>
#include "FormatPlugin.h"

class PluginManager {
public:
    /**
     * 创建解析器
     * @param type：解析器类型
     * @return
     */
    static std::shared_ptr<FormatPlugin> createFormatPlugin(const std::string &type) const;

    /**
     * 获取支持的解析格式
     */
    static void readSupportFormat(std::vector<const std::string> &formats) const;

    /**
     * 是否是支持的编码类型
     * @param format 编码类型
     * @return
     */
    static bool isSupportFormat(const std::string &format) const;

private:
    // 编码类型列表
    static std::vector<const std::string> sFormatTypeList;
};

#endif //NBREADER_PLUGINMANAGER_H
