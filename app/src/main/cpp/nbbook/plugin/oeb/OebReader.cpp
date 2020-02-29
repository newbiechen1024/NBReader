// author : newbiechen
// date : 2020-02-23 11:42
// description : 
//

#include "OebReader.h"
#include "../../tools/xhtml/XHTMLReader.h"
#include "../../filesystem/FileSystem.h"

OebReader::OebReader() : mXhtmlReader(mBookEncoder, 0) {
}

bool OebReader::readContent(TextChapter &inChapter, TextContent &outContent) {
    // 准备文件信息
    File chapterFile(inChapter.url);

    std::string chapterPath = chapterFile.getPath();
    int referenceIndex = chapterPath.rfind(FileSystem::archiveSeparator);

    // 参考名，就是文件在压缩包内的路径。(这部分是不是可能封装到什么地方)
    std::string referenceName("");
    if (referenceIndex != -1) {
        referenceName.assign(chapterPath.substr(referenceIndex + 1));
    }
    // 打开书籍编码器
    mBookEncoder.open();
    // 添加初始段落样式
    mBookEncoder.pushTextKind(TextKind::REGULAR);
    // 进行文件解析
    mXhtmlReader.readFile(chapterFile, referenceName);
    // 设置文本结束段落标记
    mBookEncoder.insertEndOfSectionParagraph();
    outContent = mBookEncoder.close();
    // 将解析到的数据输出
    return outContent.isInitialized();
}