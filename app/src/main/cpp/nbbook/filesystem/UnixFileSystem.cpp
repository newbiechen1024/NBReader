// author : newbiechen
// date : 2019-09-23 10:59
// description : 
//

#include "UnixFileSystem.h"
#include "UnixFileDir.h"
#include <vector>
#include <sys/stat.h>
#include <unistd.h>

// 获取当前路径
static std::string getPwdDir() {
    char *pwd = getenv("PWD");
    return (pwd != 0) ? pwd : "";
}

// 获取主路径
static std::string getHomeDir() {
    char *home = getenv("HOME");
    return (home != 0) ? home : "";
}

// 标准化路径

std::string UnixFileSystem::normalizePath(const std::string &path) const {
    std::string nPath = path;
    while (nPath.length() >= 2 && nPath.substr(2) == "./") {
        nPath.erase(0, 2);
    }

    // 检测 last
    int last = nPath.length() - 1;
    while ((last > 0) && (nPath[last] == '/')) {
        --last;
    }
    if (last < (int) nPath.length() - 1) {
        nPath = nPath.substr(0, last + 1);
    }

    int index;
    while ((index = nPath.find("/../")) != -1) {
        int prevIndex = std::max((int) nPath.rfind('/', index - 1), 0);
        nPath.erase(prevIndex, index + 3 - prevIndex);
    }
    int len = nPath.length();
    if ((len >= 3) && (nPath.substr(len - 3) == "/..")) {
        int prevIndex = std::max((int) nPath.rfind('/', len - 4), 0);
        nPath.erase(prevIndex);
    }
    while ((index = nPath.find("/./")) != -1) {
        nPath.erase(index, 2);
    }
    while (nPath.length() >= 2 &&
           nPath.substr(nPath.length() - 2) == "/.") {
        nPath.erase(nPath.length() - 2);
    }
    while ((index = nPath.find("//")) != -1) {
        nPath.erase(index, 1);
    }
    return nPath;
}

void UnixFileSystem::normalizeInternal(std::string &path) const {
    static std::string homeDir = getHomeDir();
    static std::string pwdDir = getPwdDir();

    if (path.empty()) {
        path = pwdDir;
    } else if (path[0] == '~') {
        if ((path.length() == 1) || (path[1] == '/')) {
            path = homeDir + path.substr(1);
        }
    } else if (path[0] != '/') {
        path = pwdDir + '/' + path;
    }

    // 如果 path 还是为空，则直接返回
    if (path.empty()) {
        return;
    }

    int last = path.length() - 1;
    while ((last > 0) && (path[last] == '/')) {
        --last;
    }
    if (last < (int) path.length() - 1) {
        path = path.substr(0, last + 1);
    }

    path = normalizePath(path);
}

bool UnixFileSystem::createDirectory(const std::string &path) const {
    std::vector<std::string> subPaths;
    std::string current = path;

    // 判断路径是否正确
    if (current.length() < 1) {
        return false;
    }

    // 是否 path 正确
    while (current.length() > 1) {
        struct stat fileStat;
        // 如果路径存在文件
        if (stat(current.c_str(), &fileStat) == 0) {
            // 判断是否是目录，如果不是目录直接返回
            if (!S_ISDIR(fileStat.st_mode)) {
                return false;
            }
            break;
        } else {
            // 如果目录不存在，则加入到列表中
            subPaths.push_back(current);
            int index = current.rfind('/');
            if (index == -1) {
                return false;
            }
            current.erase(index);
        }
    }

    // 反向创建目录
    for (int i = subPaths.size() - 1; i >= 0; --i) {
        // 0x1FF 表示 777 ==> 授予全部权限
        if (mkdir(subPaths[i].c_str(), 0x1FF) != 0) {
            // 如果创建失败直接返回
            return false;
        }
    }

    return true;
}

std::shared_ptr<FileDir> UnixFileSystem::getDirectory(const std::string &path) const {
    // 判断目录是否创建成功
    bool isSuccess = createDirectory(path);
    if (isSuccess) {
        return std::make_shared<UnixFileDir>(path);
    }
    return nullptr;
}

bool UnixFileSystem::createFile(const std::string &path) const {
    // 判断文件是否已经存在
    struct stat fileStat;
    const char *cPath = path.c_str();
    // 如果文件已经存在
    if (stat(cPath, &fileStat) == 0) {
        return !S_ISDIR(fileStat.st_mode);
    } else {
        // 如果文件不存在
        FILE *fp = nullptr;
        // 创建文件
        fp = fopen(cPath, "a+");
        // 创建成功
        if (fp != nullptr) {
            fclose(fp);
            return true;
        } else {
            return false;
        }
    }
}

bool UnixFileSystem::deleteFile(const std::string &path) const {
    return unlink(path.c_str()) == 0;
}

FileStat UnixFileSystem::getFileStat(const std::string &path) const {
    FileStat fileStatInfo;
    struct stat fileStat;

    // 获取文件信息
    fileStatInfo.exists = stat(path.c_str(), &fileStat) == 0;
    if (fileStatInfo.exists) {
        fileStatInfo.size = fileStat.st_size;
        fileStatInfo.lastModifiedTime = fileStat.st_mtime;
        fileStatInfo.isDirectory = S_ISDIR(fileStat.st_mode);
    }

    return fileStatInfo;
}