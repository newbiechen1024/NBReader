// author : newbiechen
// date : 2020/3/1 8:23 PM
// description : 段落标签
//

#ifndef NBREADER_PARAGRAPHTAG_H
#define NBREADER_PARAGRAPHTAG_H


#include "TextTag.h"
#include "../entity/TextParagraph.h"

class ParagraphTag : public TextTag {
public:
    ParagraphTag(TextParagraph::Type type);

protected:
    void writeToParcelInternal(Parcel &parcel) const override;

private:
    TextParagraph::Type type;
};


#endif //NBREADER_PARAGRAPHTAG_H
