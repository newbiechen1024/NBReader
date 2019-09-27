// author : newbiechen
// date : 2019-09-24 17:29
// description : 
//

#include "Logger.h"

void Logger::printLogger(android_LogPriority logPriority, const std::string &subTag, const std::string &msg) {
    std::string printStr = "[" + subTag + "]" + msg;
    __android_log_print(logPriority, TAG.c_str(), "%s", printStr.c_str());
}

void Logger::i(const std::string &subTag, const std::string &msg) const {
    printLogger(ANDROID_LOG_INFO, subTag, msg);
}

void Logger::d(const std::string &subTag, const std::string &msg) const {
    printLogger(ANDROID_LOG_DEBUG, subTag, msg);
}

void Logger::w(const std::string &subTag, const std::string &msg) const {
    printLogger(ANDROID_LOG_WARN, subTag, msg);

}

void Logger::e(const std::string &subTag, const std::string &msg) const {
    printLogger(ANDROID_LOG_ERROR, subTag, msg);
}