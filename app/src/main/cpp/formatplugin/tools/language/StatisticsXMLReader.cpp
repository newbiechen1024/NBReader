// author : newbiechen
// date : 2019-10-17 18:36
// description : 
//

#include "StatisticsXMLReader.h"

const std::string StatisticsXMLReader::STATISTICS_TAG = "statistics";
const std::string StatisticsXMLReader::ITEM_TAG = "item";
void StatisticsXMLReader::startElementHandler(const char *tag, const char **attributes) {

}

std::shared_ptr<XMLStatisticsTag> StatisticsXMLReader::readStatisticsTag(const std::string &fileName) {
    return std::make_shared<XMLStatisticsTag>();
}