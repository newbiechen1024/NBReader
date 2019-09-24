// author : newbiechen
// date : 2019-09-22 19:48
// description : 
//

#ifndef NBREADER_FILE_H
#define NBREADER_FILE_H

#include <string>

class File {
public:
    // 压缩类型
    enum ArchiveType {
        NONE = 0,
        GZIP = 0x0001,
        ZIP = 0x0100,
        TAR = 0x200,
        ARCHIVE = 0xff00,
    };

    File(const std::string &path);

    ~File();

    // 文件是否存在
    bool exists() const;

    // 获取文件名,如：xx/xx/file.txt ，返回 file
    std::string &getName() const;

    // 获取完整名称，如：xx/xx/file.txt ，返回 file.txt
    std::string &getFullName() const;

    // 获取扩展名，如：xx/xx/file.txt ，返回 txt
    std::string *getExtension() const;

    // 获取绝对路径
    std::string &getAbsolutePath() const;

    // 判断是否是目录
    bool isDirectory() const;

    // 判断是否是压缩文件
    bool isArchive() const;

    // 判断是否是文件
    bool isFile() const;

    // 最后的修改时间
    std::size_t lastModified() const;

    // 文件的大小
    std::size_t length() const;

    // 创建文件
    bool createFile() const;

    // 删除文件
    bool deleteFile() const;

    // TODO:返回文件夹的内容 ==> 怎么返回是个问题
    std::vector<File *> list() const;

    // 创建整个文件目录
    bool mkdirs() const;

    bool operator==(const File &other) const;

    bool operator!=(const File &other) const;

private:
    std::string mPath;
    std::string mName;
    std::string mFullName;
    std::string mExtension;
};


#endif //NBREADER_FILE_H
