// author : newbiechen
// date : 2019-09-25 15:19
// description : 文件目录结构类
//

#ifndef NBREADER_FILEDIR_H
#define NBREADER_FILEDIR_H

#include <string>
#include <vector>

class FileDir {

public:
    ~FileDir() {
    }

    // 获取目录下的文件名
    void readFileNames(std::vector<std::string> &names) const;

    // 获取文件路径
    void readFilePaths(std::vector<std::string> &paths) const;

    const std::string &getPath() const {
        return mPath;
    }

    // 将 fileName 转成 Path
    std::string fileNameToPath(const std::string &name) const;

protected:
    FileDir(const std::string &path);

    virtual void readFilePaths(std::vector<std::string> &paths, bool fullPath) const = 0;

    virtual std::string getSeparator() const;

private:
    std::string mPath;

/*    FileDir(const FileDir &);

    const FileDir &operator=(const FileDir &);*/
};

#endif //NBREADER_FILEDIR_H
