# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
# 当前 Cmake 只支持 android 平台
cmake_minimum_required(VERSION 3.4.1)

# 添加 expat 库
include_directories(include)

# 设置三方库路径
set(ABI_PATH ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})
# 设置代码路径
set(CODE_PATH ./nbbook)

# 添加 xml 解析库
add_library(
        expat
        STATIC
        IMPORTED
)

set_target_properties(
        expat
        PROPERTIES
        IMPORTED_LOCATION ${ABI_PATH}/libexpat.a
)

# 正则表达式
add_library(
        onig
        SHARED
        IMPORTED
)

set_target_properties(
        onig
        PROPERTIES
        IMPORTED_LOCATION ${ABI_PATH}/libonig.so
)

# 编码转换
add_library(
        iconv
        SHARED
        IMPORTED
)

set_target_properties(
        iconv
        PROPERTIES
        IMPORTED_LOCATION ${ABI_PATH}/libiconv.so
)

# 单词分割
add_library(
        linebreak
        SHARED
        IMPORTED
)

set_target_properties(
        linebreak
        PROPERTIES
        IMPORTED_LOCATION ${ABI_PATH}/libLineBreak.so
)

# 字符编码检测
add_library(
        uchardet
        STATIC
        IMPORTED
)

set_target_properties(
        uchardet
        PROPERTIES
        IMPORTED_LOCATION ${ABI_PATH}/libuchardet.a
)

# android 自带的 zip 压缩文件库
find_library(
        zip-lib
        z)

# android 自带的日志库
find_library( # Sets the title of the path variable.
        log-lib
        # Specifies the title of the NDK library that
        # you want CMake to locate.
        log)

# 获取目录下的 c++ 文件写入到变量中
aux_source_directory(${CODE_PATH} ROOT_SOURCE)
aux_source_directory(${CODE_PATH}/util UTIL_SOURCE)
aux_source_directory(${CODE_PATH}/util/regex UTIL_REGEX_SOURCE)
aux_source_directory(${CODE_PATH}/filesystem FILE_SYSTEM_SOURCE)
aux_source_directory(${CODE_PATH}/filesystem/io FILE_SYSTEM_IO_SOURCE)
aux_source_directory(${CODE_PATH}/filesystem/zip FILE_SYSTEM_ZIP_SOURCE)
aux_source_directory(${CODE_PATH}/filesystem/buffer FILE_SYSTEM_BUFFER_SOURCE)
aux_source_directory(${CODE_PATH}/filesystem/charset FILE_SYSTEM_CHARSET_SOURCE)
aux_source_directory(${CODE_PATH}/plugin PLUGIN_SOURCE)
aux_source_directory(${CODE_PATH}/plugin/txt TXT_PLUGIN_SOURCE)
aux_source_directory(${CODE_PATH}/plugin/oeb OEB_PLUGIN_SOURCE)
aux_source_directory(${CODE_PATH}/reader/text READER_TEXT_SOURCE)
aux_source_directory(${CODE_PATH}/reader/text/tag READER_TEXT_TAG_SOURCE)
aux_source_directory(${CODE_PATH}/reader/text/entity READER_TEXT_ENTITY_SOURCE)
aux_source_directory(${CODE_PATH}/reader/text/type READER_TEXT_TYPE_SOURCE)
aux_source_directory(${CODE_PATH}/reader/book BOOK_READER_SOURCE)
aux_source_directory(${CODE_PATH}/android/filesystem ANDROID_FILE_SYSTEM_SOURCE)
aux_source_directory(${CODE_PATH}/android/application ANDROID_APPLICATION_SOURCE)
aux_source_directory(${CODE_PATH}/tools/font TOOLS_FONT_SOURCE)
aux_source_directory(${CODE_PATH}/tools/encoding ENCODING_TOOLS_SOURCE)
aux_source_directory(${CODE_PATH}/tools/language LANGUAGE_TOOLS_SOURCE)
aux_source_directory(${CODE_PATH}/tools/xml XML_TOOLS_SOURCE)
aux_source_directory(${CODE_PATH}/tools/css CSS_TOOLS_SOURCE)
aux_source_directory(${CODE_PATH}/tools/drm DRM_TOOLS_SOURCE)


aux_source_directory(${CODE_PATH}/filesystem/asset FILE_SYSTEM_ASSET_SOURCE)


add_library( # Sets the title of the library.
        nbbook
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${ROOT_SOURCE}
        ${FILE_SYSTEM_SOURCE}
        ${FILE_SYSTEM_ZIP_SOURCE}
        ${FILE_SYSTEM_IO_SOURCE}
        ${FILE_SYSTEM_ASSET_SOURCE}
        ${ANDROID_FILE_SYSTEM_SOURCE}
        ${ANDROID_APPLICATION_SOURCE}
        ${UTIL_SOURCE}
        ${UTIL_REGEX_SOURCE}
        ${PLUGIN_SOURCE}
        ${TXT_PLUGIN_SOURCE}
        ${OEB_PLUGIN_SOURCE}
        ${BOOK_READER_SOURCE}
        ${TOOLS_FONT_SOURCE}
        ${XML_TOOLS_SOURCE}
        ${CSS_TOOLS_SOURCE}
        ${DRM_TOOLS_SOURCE}
        ${ENCODING_TOOLS_SOURCE}
        ${LANGUAGE_TOOLS_SOURCE}
        ${FILE_SYSTEM_CHARSET_SOURCE}
        ${FILE_SYSTEM_BUFFER_SOURCE}
        ${READER_TEXT_SOURCE}
        ${READER_TEXT_TAG_SOURCE}
        ${READER_TEXT_ENTITY_SOURCE}
        ${READER_TEXT_TYPE_SOURCE})

target_link_libraries(
        nbbook
        android
        expat
        uchardet
        onig
        iconv
        linebreak
        ${log-lib}
        ${zip-lib}
)