// author : newbiechen
// date : 2020-02-15 21:27
// description : 
//

#ifndef NBREADER_TEXTALIGNMENTTYPE_H
#define NBREADER_TEXTALIGNMENTTYPE_H

enum class TextAlignmentType : char {
    ALIGN_UNDEFINED = 0,
    ALIGN_LEFT = 1,
    ALIGN_RIGHT = 2,
    ALIGN_CENTER = 3,
    ALIGN_JUSTIFY = 4,
    ALIGN_LINESTART = 5 // left for LTR languages and right for RTL
};


#endif //NBREADER_TEXTALIGNMENTTYPE_H
