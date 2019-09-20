# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 获取 c++ 文件写入到变量中
aux_source_directory(. DIR_SOURCE)
aux_source_directory(./util UTIL_SOURCE)
aux_source_directory(./plugin PLUGIN_SOURCE)
aux_source_directory(./entity/book BOOK_ENTITY_SOURCE)
aux_source_directory(./entity/bookmodel BOOK_MODEL_ENTITY_SOURCE)
aux_source_directory(./entity/textmodel TEXT_MODEL_ENTITY_SOURCE)

add_library( # Sets the name of the library.
        FormatPlugin
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${DIR_SOURCE} ${PLUGIN_SOURCE} ${UTIL_SOURCE}
        ${BOOK_ENTITY_SOURCE} ${BOOK_MODEL_ENTITY_SOURCE}
        ${TEXT_MODEL_ENTITY_SOURCE})