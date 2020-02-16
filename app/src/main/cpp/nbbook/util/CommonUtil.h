// author : newbiechen
// date : 2020-02-16 14:38
// description : 
//

#ifndef NBREADER_COMMONUTIL_H
#define NBREADER_COMMONUTIL_H

#include <type_traits>

namespace CommonUtil {
    template<typename E>
    constexpr typename std::underlying_type<E>::type to_underlying(E e) noexcept {
        return static_cast<typename std::underlying_type<E>::type>(e);
    };
}

#endif //NBREADER_COMMONUTIL_H
