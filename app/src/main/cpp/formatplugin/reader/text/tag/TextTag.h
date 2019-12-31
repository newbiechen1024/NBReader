// author : newbiechen
// date : 2019-12-30 14:31
// description : 文本标签
//

#ifndef NBREADER_TEXTTAG_H
#define NBREADER_TEXTTAG_H


class TextTag {
protected:
    TextTag() {
    }

public:
    virtual ~TextTag() {
    }

private: // 禁止复制
    TextTag(const TextTag &entry);

    const TextTag &operator=(const TextTag &entry);
};

#endif //NBREADER_TEXTTAG_H
