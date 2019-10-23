// author : newbiechen
// date : 2019-10-21 19:48
// description : 
//

#include <jni.h>
#include <linebreak/linebreak.h>

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_text_processor_LineBreaker_init(JNIEnv *env, jobject instance) {

    init_linebreak();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_text_processor_LineBreaker_setLineBreakForCharArray(JNIEnv *env,
                                                                                                           jobject instance,
                                                                                                           jcharArray data_,
                                                                                                           jint offset,
                                                                                                           jint length,
                                                                                                           jstring lang_,
                                                                                                           jbyteArray breaks_) {
    jchar *dataArr = env->GetCharArrayElements(data_, NULL);
    jbyte *breakArr = env->GetByteArrayElements(breaks_, NULL);
    const char *lang = env->GetStringUTFChars(lang_, 0);

    // TODO
    // 根据语言类型，重置字符串中的 BREAK 类型，实现换行
    set_linebreaks_utf16(dataArr + offset, length, lang, (char *) breakArr);

    const jchar *start = dataArr + offset;
    const jchar *end = start + length;
    for (const jchar *ptr = start; ptr < end; ++ptr) {
        if (*ptr == (jchar) 0xAD) {
            dataArr[ptr - start] = LINEBREAK_NOBREAK;
        }
    }

    env->ReleaseCharArrayElements(data_, dataArr, 0);
    env->ReleaseStringUTFChars(lang_, lang);
    env->ReleaseByteArrayElements(breaks_, breakArr, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_newbiechen_nbreader_ui_component_book_text_processor_LineBreaker_setLineBreakForString(JNIEnv *env,
                                                                                                        jobject instance,
                                                                                                        jstring data_,
                                                                                                        jstring lang_,
                                                                                                        jbyteArray breaks_) {
    const jchar *dataArr = env->GetStringChars(data_, 0);
    const char *lang = env->GetStringUTFChars(lang_, 0);
    jbyte *breakArr = env->GetByteArrayElements(breaks_, NULL);

    const size_t dataLength = env->GetStringLength(data_);

    set_linebreaks_utf16(dataArr, dataLength, lang, (char *) breakArr);

    env->ReleaseStringChars(data_, dataArr);
    env->ReleaseStringUTFChars(lang_, lang);
    env->ReleaseByteArrayElements(breaks_, breakArr, 0);
}