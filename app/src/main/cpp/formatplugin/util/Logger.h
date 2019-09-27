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
    static const std::string TAG = "NBReader";
    static void i(const std::string &subTag,const std::string &msg) const;
    static void w(const std::string &subTag,const std::string &msg) const;
    static void d(const std::string &subTag,const std::string &msg) const;
    static void e(const std::string &subTag,const std::string &msg) const;
private:
    Logger();

    static void printLogger(android_LogPriority logPriority, const std::string &subTag, const std::string &msg);
};


#endif //NBREADER_LOGGER_H
