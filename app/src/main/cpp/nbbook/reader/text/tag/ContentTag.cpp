// author : newbiechen
// date : 2020/3/1 9:49 PM
// description : 
//

#include "ContentTag.h"

ContentTag::ContentTag(const std::string &text) : TextTag(TextTagType::TEXT) {
    append(text);
}

ContentTag::ContentTag(const std::vector<std::string> &texts) : TextTag(TextTagType::TEXT) {
    append(texts);
}

void ContentTag::append(const std::string &text) {
    mContentList.push_back(text);
}

void ContentTag::append(const std::vector<std::string> &texts) {
    mContentList.insert(mContentList.end(), texts.begin(), texts.end());
}

void ContentTag::writeToParcelInternal(Parcel &parcel) const {
    std::string resultText;

    // 将数据中的数据，写入到统一文本中
    for (auto itr = mContentList.begin(); itr < mContentList.end(); ++itr) {
        resultText.append(*itr);
    }

    // 写入文本数据
    parcel.writeString32(resultText);
}
