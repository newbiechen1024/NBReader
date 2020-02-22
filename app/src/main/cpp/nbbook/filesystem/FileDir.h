// author : newbiechen
// date : 2019-09-25 15:19
// description : 文件目录结构类
//

#ifndef NBREADER_FILEDIR_H
#define NBREADER_FILEDIR_H

#include <vector>
#include <string>

class FileDir {

public:
    ~FileDir() {
    }

    /**
     * 获取目录下的文件名
     * 如：path/xxx，得 xxx
     * 对于 zip dir 返回的应该是子目录
     * 如：xxx.zip 文件，包含 path/test 这个文件，得到的 name 就是 path/test
     * @param names
     */
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

    /**
     * 读取目录下的文件路径
     * @param paths：
     * @param fullPath：是否是完整的路径
     */
    virtual void readFilePaths(std::vector<std::string> &paths, bool fullPath) const = 0;

    virtual std::string getSeparator() const;

private:
    std::string mPath;

/*    FileDir(const FileDir &);

    const FileDir &operator=(const FileDir &);*/
};

#endif //NBREADER_FILEDIR_H
