// author : newbiechen
// date : 2020-02-15 09:51
// description : 
//

#include "OebPlugin.h"
#include "NcxReader.h"
#include "../../tools/xml/SAXParserFactory.h"
#include "ContainerReader.h"
#include "../../util/StringUtil.h"
#include "../../util/Logger.h"
#include "../../filesystem/FileSystem.h"

static const std::string SUFFIX_OPF = "opf";
static const std::string EXTENSTION_OPF = ".opf";

static const std::string TAG = "OebPlugin";

// TODO:FBReader OEB 支持的功能，现在暂时不支持
// readMetadata：作者信息之类的
// readUids：epub 文件在文件图书馆的唯一 id
// readEncryptionInfos：加密信息。

OebPlugin::OebPlugin() {

}

OebPlugin::~OebPlugin() {
}

void OebPlugin::onInit() {
    // 获取 opf 文件
    File opfFile = findOpfFile(getBookFile());
    // 解析 opf 文件
    mOpfReader.readFile(opfFile);
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
        if (StringUtil::endsWith(*it, EXTENSTION_OPF)) {
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
    // TODO:读取编码信息(暂时采用默认值)
    outEncoding = "utf-8";
    return true;
}

bool OebPlugin::readLanguageInternal(std::string &outLanguage) {
    // TODO:读取语言信息(暂时采用默认值)
    outLanguage = "zh";
    return true;
}

bool OebPlugin::readChaptersInternal(std::string &chapterPattern,
                                     std::vector<TextChapter> &chapterList) {
    std::string ncxPath =
            mOpfReader.getOpfDirPath() + mOpfReader.getNcxFileName();
    File ncxFile(ncxPath);
    // 根据从 opf 中获取到的信息，获取 .ncx 文件
    NcxReader ncxReader;
    // 读取文件解析，是否需要返回什么东西
    ncxReader.readFile(ncxFile);
    // 获取 ncv 解析后的数据，导航定位
    auto navPointMap = ncxReader.navigationMap();
    // 获取 ncx 目录地址
    std::string ncxDirPath = ncxReader.getNcxDirPath();
    // TODO:不处理副标题问题，直接上层解析。
    // 转换成 TextChapter
    for (auto it = navPointMap.begin(); it != navPointMap.end(); ++it) {
        auto point = (*it).second;
        TextChapter chapter(ncxDirPath + point.ContentHRef,
                            point.Text, 0, -1);
        // 添加到章节目录中
        chapterList.push_back(chapter);
    }

    return true;
}

bool OebPlugin::readChapterContentInternal(TextChapter &inChapter, TextContent &outContent) {
    Logger::i(TAG, "readChapterContentInternal txtChapter" + inChapter.toString());
    return mOebReader.readContent(inChapter, outContent);;
}
