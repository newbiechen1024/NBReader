# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
# 当前 Cmake 只支持 android 平台
cmake_minimum_required(VERSION 3.4.1)

# 添加 expat 库
include_directories(include)

set(ABI_PATH ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI})

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

find_library(
        zip-lib
        z)

find_library( # Sets the title of the path variable.
        log-lib
        # Specifies the title of the NDK library that
        # you want CMake to locate.
        log)

# 获取目录下的 c++ 文件写入到变量中
aux_source_directory(. DIR_SOURCE)
aux_source_directory(./util UTIL_SOURCE)
aux_source_directory(./filesystem FILE_SYSTEM_SOURCE)
aux_source_directory(./filesystem/io FILE_SYSTEM_IO_SOURCE)
aux_source_directory(./filesystem/zip FILE_SYSTEM_ZIP_SOURCE)
aux_source_directory(./plugin PLUGIN_SOURCE)
aux_source_directory(./plugin/txt TXT_PLUGIN_SOURCE)
aux_source_directory(./reader/book BOOK_READER_SOURCE)
aux_source_directory(./reader/bookmodel BOOK_MODEL_READER_SOURCE)
aux_source_directory(./reader/textmodel TEXT_MODEL_READER_SOURCE)
aux_source_directory(./android/filesystem ANDROID_FILE_SYSTEM_SOURCE)
aux_source_directory(./android/application ANDROID_APPLICATION_SOURCE)
aux_source_directory(./tools/font TOOLS_FONT_SOURCE)
aux_source_directory(./tools/encoding TOOLS_ENCODING_SOURCE)
aux_source_directory(./tools/language TOOLS_LANGUAGE_SOURCE)
aux_source_directory(./tools/xml TOOLS_XML_SOURCE)
aux_source_directory(./filesystem/asset FILE_SYSTEM_ASSET_SOURCE)

add_library( # Sets the title of the library.
        FormatPlugin
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${DIR_SOURCE}
        ${FILE_SYSTEM_SOURCE}
        ${FILE_SYSTEM_ZIP_SOURCE}
        ${FILE_SYSTEM_IO_SOURCE}
        ${FILE_SYSTEM_ASSET_SOURCE}
        ${ANDROID_FILE_SYSTEM_SOURCE}
        ${ANDROID_APPLICATION_SOURCE}
        ${UTIL_SOURCE}
        ${PLUGIN_SOURCE}
        ${TXT_PLUGIN_SOURCE}
        ${BOOK_READER_SOURCE}
        ${BOOK_MODEL_READER_SOURCE}
        ${TEXT_MODEL_READER_SOURCE}
        ${TOOLS_FONT_SOURCE}
        ${TOOLS_XML_SOURCE}
        ${TOOLS_ENCODING_SOURCE}
        ${TOOLS_LANGUAGE_SOURCE})

target_link_libraries(
        FormatPlugin
        ${log-lib}
        ${zip-lib}
        android
        expat
)