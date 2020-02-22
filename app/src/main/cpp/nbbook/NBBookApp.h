// author : newbiechen
// date : 2019-10-06 19:18
// description : 全局上下文抽象类
//

#ifndef NBREADER_NBBOOKAPP_H
#define NBREADER_NBBOOKAPP_H

#include <string>
#include <jni.h>

class NBBookApp {
public:
    static NBBookApp &getInstance() {
        return *sInstance;
    }

    static void deleteInstance();

    // 初始应用
    virtual void initApp(JavaVM *jvm) = 0;

    // 获取平台对应的语言
    virtual std::string language() = 0;

    // 获取平台对应的版本
    virtual std::string version() = 0;

protected:
    static NBBookApp *sInstance;

    NBBookApp() {}

    virtual ~NBBookApp() {}
};

#endif //NBREADER_NBBOOKAPP_H
