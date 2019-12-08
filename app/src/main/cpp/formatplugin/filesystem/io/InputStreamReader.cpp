// author : newbiechen
// date : 2019-11-29 23:39
// description : 
//

#include "InputStreamReader.h"


InputStreamReader::InputStreamReader(std::shared_ptr<InputStream> inputStream,
                                     const std::string &charset)
        : mStreamDecoder(inputStream, charset) {
}

InputStreamReader::~InputStreamReader() {
}

bool InputStreamReader::open() {
    return mStreamDecoder.open();
}

int InputStreamReader::read(char *buffer, size_t length) {
    return mStreamDecoder.read(buffer, length);
}

void InputStreamReader::close() {
    mStreamDecoder.close();
}

bool InputStreamReader::isFinish() const {
    return mStreamDecoder.isFinish();
}
