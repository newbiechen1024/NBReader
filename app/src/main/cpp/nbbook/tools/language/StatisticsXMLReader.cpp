// author : newbiechen
// date : 2019-10-17 18:36
// description : 
//

#include "StatisticsXMLReader.h"
#include "../../filesystem/asset/AssetManager.h"

const std::string StatisticsXMLReader::STATISTICS_TAG = "statistics";
const std::string StatisticsXMLReader::ITEM_TAG = "item";

std::map<std::string, std::shared_ptr<XMLStatisticsTag>> StatisticsXMLReader::statisticsMap;

void StatisticsXMLReader::startElementHandler(const char *tag, const char **attributes) {
    if (STATISTICS_TAG == tag) {
        std::size_t volume = atoi(attributeValue(attributes, "volume"));
        unsigned long long squaresVolume = atoll(attributeValue(attributes, "squaresVolume"));
        mStatisticsPtr = std::make_shared<XMLStatisticsTag>(atoi(attributeValue(attributes, "charSequenceSize")),
                                                            atoi(attributeValue(attributes, "size")), volume,
                                                            squaresVolume);
    } else if (ITEM_TAG == tag) {
        const char *sequence = attributeValue(attributes, "sequence");
        const char *frequency = attributeValue(attributes, "frequency");
        if ((sequence != 0) && (frequency != 0)) {
            std::string hexString(sequence);
            mStatisticsPtr->insert(CharSequence(hexString), atoi(frequency));
        }
    }
}

std::shared_ptr<XMLStatisticsTag> StatisticsXMLReader::readStatisticsTag(const std::string &fileName) {

    std::map<std::string, std::shared_ptr<XMLStatisticsTag> >::iterator it = statisticsMap.find(fileName);

    if (it != statisticsMap.end()) {
        return it->second;
    }

    // 从资源文件获取输入流
    std::shared_ptr<InputStream> statisticsStream = AssetManager::getInstance().open(fileName);

    readDocument(statisticsStream);

    statisticsStream->close();

    statisticsMap.insert(std::make_pair(fileName, mStatisticsPtr));

    return mStatisticsPtr;
}