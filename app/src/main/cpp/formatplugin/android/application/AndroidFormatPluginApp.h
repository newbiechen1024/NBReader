// author : newbiechen
// date : 2019-10-14 11:10
// description : 
//

#ifndef NBREADER_ANDROIDFORMATPLUGINAPP_H
#define NBREADER_ANDROIDFORMATPLUGINAPP_H


#include "../../FormatPluginApp.h"

class AndroidFormatPluginApp : FormatPluginApp {
public:
    // 创建 app
    static void newInstance();

private:
    void initApp(JavaVM *jvm) override;

public:

    std::string language() override;

    std::string version() override;

private:
    AndroidFormatPluginApp() {
    }

    ~AndroidFormatPluginApp() {
    }
};


#endif //NBREADER_ANDROIDFORMATPLUGINAPP_H
