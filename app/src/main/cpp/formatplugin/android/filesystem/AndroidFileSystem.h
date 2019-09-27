// author : newbiechen
// date : 2019-09-24 10:54
// description : Android 直接沿用 UnixFileSystem，这里只是用来占位
//

#ifndef NBREADER_ANDROIDFILESYSTEM_H
#define NBREADER_ANDROIDFILESYSTEM_H


#include <filesystem/UnixFileSystem.h>

class AndroidFileSystem: UnixFileSystem {
public:
    static void newInstance();
};

inline void AndroidFileSystem::newInstance() {
    sInstance = new AndroidFileSystem();
}

#endif //NBREADER_ANDROIDFILESYSTEM_H
