// author : newbiechen
// date : 2020-02-15 09:51
// description : 
//

#include "OebPlugin.h"
#include "NcxReader.h"
#include "../../tools/xml/SAXParserFactory.h"
#include "ContainerReader.h"
#include "../../util/StringUtil.h"

static const std::string SUFFIX_OPF = "opf";


// TODO:FBReader OEB 支持的功能，现在暂时不支持
// readMetadata：作者信息之类的
// readUids：epub 文件在文件图书馆的唯一 id
// readEncryptionInfos：加密信息。

OebPlugin::OebPlugin() {

}

OebPlugin::~OebPlugin() {
}

File OebPlugin::findOpfFile(const File &oebFile) {
    if (oebFile.getExtension() == SUFFIX_OPF) {
        return oebFile;
    }

    // 先强制将文件转换成 zip 文件
    oebFile.forceArchiveType(File::ZIP);

    std::shared_ptr<FileDir> zipDir = oebFile.getDirectory();

    if (zipDir == nullptr) {
        return File::NO_FILE;
    }

    const File containerInfoFile(zipDir->fileNameToPath("META-INF/container.xml"));
    if (containerInfoFile.exists()) {
        ContainerReader reader;
        reader.readFile(containerInfoFile);

        const std::string &opfPath = reader.getOpfPath();

        if (!opfPath.empty()) {
            return File(zipDir->fileNameToPath(opfPath));
        }
    }

    std::vector<std::string> fileNames;
    zipDir->readFileNames(fileNames);
    for (auto it = fileNames.begin(); it != fileNames.end(); ++it) {
        if (StringUtil::endsWith(*it, ".opf")) {
            return File(zipDir->fileNameToPath(*it));
        }
    }
    return File::NO_FILE;
}

File OebPlugin::findEpubFile(const File &oebFile) {
    const File epub =
            oebFile.getExtension() == SUFFIX_OPF ? File(oebFile.getArchivePkgPath()) : oebFile;
    epub.forceArchiveType(File::ZIP);
    return epub;
}

bool OebPlugin::readEncodingInternal(std::string &outEncoding) {
    // 读取编码信息
    return false;
}

bool OebPlugin::readLanguageInternal(std::string &outLanguage) {
    // 读取语言信息
    return false;
}

bool OebPlugin::readChaptersInternal(std::string &chapterPattern,
                                     std::vector<TextChapter> &chapterList) {


    NcxReader ncxReader;
    // 获取书本文件，查找其中的 opf 文件
    File opfFile = findOpfFile(getBookFile());
    // 读取文件解析，是否需要返回什么东西
    ncxReader.readFile(opfFile);
    // 获取 ncv 解析后的数据，导航定位
    auto navPointMap = ncxReader.navigationMap();
    // 获取压缩包地址
    std::string archivePkgPath = opfFile.getArchivePkgPath();

    // 转换成 TextChapter
    for (auto it = navPointMap.begin(); it != navPointMap.end(); ++it) {
        auto point = (*it).second;
        // TODO:这个 ":" 应该用一个专门的方法转换会更好吧，暂时先这样。
        TextChapter chapter(archivePkgPath + ":" + point.ContentHRef, point.Text, 0, -1);
        chapterList.push_back(chapter);
    }

    return true;
}

bool OebPlugin::readChapterContentInternal(TextChapter &txtChapter, char **outBuffer,
                                           size_t *outSize) {
    return false;
}