// author : newbiechen
// date : 2019-09-24 17:29
// description : 
//

#ifndef NBREADER_LOGGER_H
#define NBREADER_LOGGER_H

#include <string>
#include <android/log.h>

class Logger {
public:
    static const std::string TAG;

    static void i(const std::string &subTag,const std::string &msg);
    static void w(const std::string &subTag,const std::string &msg);
    static void d(const std::string &subTag,const std::string &msg);
    static void e(const std::string &subTag,const std::string &msg);
private:
    Logger();

    static void printLogger(android_LogPriority logPriority, const std::string &subTag, const std::string &msg);
};

const std::string TAG = "NBReader";

#endif //NBREADER_LOGGER_H
