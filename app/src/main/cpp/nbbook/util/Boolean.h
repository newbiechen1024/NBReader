// author : newbiechen
// date : 2020-02-15 21:43
// description : 
//

#ifndef NBREADER_BOOLEAN_H
#define NBREADER_BOOLEAN_H

enum class Boolean : char {
    FALSE = 0,
    TRUE = 1,
    UNDEFINED = 2
};

inline Boolean boolToBoolean(bool value) {
    return value ? Boolean::TRUE : Boolean::FALSE;
}

inline bool booleanToBool(Boolean value, bool defaultValue) {
    switch (value) {
        default:
            return defaultValue;
        case Boolean::TRUE:
            return true;
        case Boolean::FALSE:
            return false;
    }
}

#endif //NBREADER_BOOLEAN_H
