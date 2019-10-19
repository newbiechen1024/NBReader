// author : newbiechen
// date : 2019-10-17 18:36
// description : 对资源文件中的 xml 文件进行解析
//

#ifndef NBREADER_STATISTICSXMLREADER_H
#define NBREADER_STATISTICSXMLREADER_H


#include <tools/xml/XMLReader.h>
#include "StatisticsTag.h"

class StatisticsXMLReader : public XMLReader {
public:
    std::shared_ptr<XMLStatisticsTag> readStatisticsTag(const std::string &fileName);

    void startElementHandler(const char *tag, const char **attributes);

private:
    std::shared_ptr<XMLStatisticsTag> mStatisticsPtr;

private:
    static const std::string ITEM_TAG;
    static const std::string STATISTICS_TAG;
    static std::map<std::string, std::shared_ptr<XMLStatisticsTag> > statisticsMap;
};


#endif //NBREADER_STATISTICSXMLREADER_H
