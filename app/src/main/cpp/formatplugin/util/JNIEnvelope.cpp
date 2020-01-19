// author : newbiechen
// date : 2019-09-19 13:50
// description : 
//

#include "JNIEnvelope.h"
#include "AndroidUtil.h"

/**
 * Java Type
 */

JavaType::JavaType() {}

JavaType::~JavaType() {}

/**
 *
 * Java Array
 */

JavaArray::JavaArray(const JavaType &base) : mType(base) {}

std::string JavaArray::signature() const {
    return "[" + mType.signature();
}

/**
 * JavaBasicType
 */

const JavaBasicType JavaBasicType::Void("V");
const JavaBasicType JavaBasicType::Int("I");
const JavaBasicType JavaBasicType::Long("J");
const JavaBasicType JavaBasicType::Boolean("Z");
const JavaBasicType JavaBasicType::Float("F");
const JavaBasicType JavaBasicType::Double("S");

/**
 * Java String
 */

JavaString::JavaString(JNIEnv *env, const std::string &str, bool emptyIsNull) {
    mJString = (emptyIsNull && str.empty()) ? 0 : env->NewStringUTF(str.c_str());
}

JavaString::~JavaString() {
    if (mJString != 0) {
        mEnv->DeleteLocalRef(mJString);
    }
}

/**
 * Java Class
 */

JavaClass::JavaClass(const std::string &name) : mName(name) {
    mJClass = 0;
}

JavaClass::~JavaClass() {
    if (mJClass != 0) {
        AndroidUtil::getEnv()->DeleteLocalRef(mJClass);
    }
}

std::string JavaClass::signature() const {
    return "L" + mName + ";";
}

jclass JavaClass::getJClass() const {
    if (mJClass == 0) {
        JNIEnv *env = AndroidUtil::getEnv();
        jclass ref = env->FindClass(mName.c_str());
        // 将类指针设置为全局
        mJClass = (jclass) env->NewGlobalRef(ref);
        env->DeleteLocalRef(ref);
    }
    return mJClass;
}

/**
 * Java Any
 */

JavaAny::JavaAny(const JavaClass &cls) : mClass(cls) {}

JavaAny::~JavaAny() {}

/**
 * Java Constructor
 */

JavaConstructor::JavaConstructor(const JavaClass &cls, const std::string &parameters) : JavaAny(
        cls) {
    char name[] = "<init>";
    mId = AndroidUtil::getEnv()->GetMethodID(getJClass(), name, parameters.c_str());
}

jobject JavaConstructor::call(...) {
    va_list list;
    // 获取 ... 中的参数
    va_start(list, this);
    // 将 list 中的参数传递给构造方法
    jobject obj = AndroidUtil::getEnv()->NewObjectV(getJClass(), mId, list);
    va_end(list);
    return obj;
}

/**
 * Java Field
 */

JavaField::JavaField(const JavaClass &cls, const std::string &name, const JavaType &type) : JavaAny(
        cls), mName(name) {
    mId = AndroidUtil::getEnv()->GetFieldID(getJClass(), mName.c_str(), type.signature().c_str());
}

JavaField::~JavaField() {
}

/**
 * Java Method
 */

JavaMethod::JavaMethod(const JavaClass &cls, const std::string &name, const JavaType &returnType,
                       const std::string &parameters) : JavaAny(cls), mName(name) {
    // 生成新的签名
    const std::string signature = parameters + returnType.signature();
    mId = AndroidUtil::getEnv()->GetMethodID(getJClass(), name.c_str(), signature.c_str());
}

JavaMethod::~JavaMethod() {
}

/**
 * Static Method
 */

JavaStaticMethod::JavaStaticMethod(const JavaClass &cls, const std::string &name,
                                   const JavaType &returnType,
                                   const std::string &param) : JavaAny(cls), mName(name) {
    const std::string signature = param + returnType.signature();
    mId = AndroidUtil::getEnv()->GetStaticMethodID(getJClass(), name.c_str(), signature.c_str());
}

JavaStaticMethod::~JavaStaticMethod() {
}

/**
 * Object Method
 */

ObjectField::ObjectField(const JavaClass &cls, const std::string &name, const JavaType &type)
        : JavaField(cls, name, type) {
}

jobject ObjectField::value(jobject obj) const {
    jobject val = AndroidUtil::getEnv()->GetObjectField(obj, mId);
    return val;
}

VoidMethod::VoidMethod(const JavaClass &cls, const std::string &name, const std::string &parameters)
        : JavaMethod(cls, name, JavaBasicType::Void, parameters) {
}

void VoidMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    AndroidUtil::getEnv()->CallVoidMethodV(base, mId, lst);
    va_end(lst);
}

IntMethod::IntMethod(const JavaClass &cls, const std::string &name, const std::string &parameters)
        : JavaMethod(cls, name, JavaBasicType::Int, parameters) {
}

jint IntMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jint result = AndroidUtil::getEnv()->CallIntMethodV(base, mId, lst);
    va_end(lst);
    return result;
}

LongMethod::LongMethod(const JavaClass &cls, const std::string &name, const std::string &param)
        : JavaMethod(cls, name, JavaBasicType::Long, param) {
}

jlong LongMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jlong result = AndroidUtil::getEnv()->CallLongMethodV(base, mId, lst);
    va_end(lst);
    return result;
}

BooleanMethod::BooleanMethod(const JavaClass &cls, const std::string &name,
                             const std::string &parameters) : JavaMethod(cls, name,
                                                                         JavaBasicType::Boolean,
                                                                         parameters) {
}

jboolean BooleanMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jboolean result = AndroidUtil::getEnv()->CallBooleanMethodV(base, mId, lst);
    va_end(lst);
    return result;
}

static JavaBasicType FakeString("Ljava/lang/String;");

StringMethod::StringMethod(const JavaClass &cls, const std::string &name,
                           const std::string &parameters) : JavaMethod(cls, name, FakeString,
                                                                       parameters) {
}

jstring StringMethod::callForJavaString(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jstring result = (jstring) AndroidUtil::getEnv()->CallObjectMethodV(base, mId, lst);

    va_end(lst);
    return result;
}

std::string StringMethod::callForCppString(jobject base, ...) {
    JNIEnv *env = AndroidUtil::getEnv();
    va_list lst;
    va_start(lst, base);
    jstring j = (jstring) env->CallObjectMethodV(base, mId, lst);
    va_end(lst);
    std::string str = AndroidUtil::toCString(env, j);
    if (j != 0) {
        env->DeleteLocalRef(j);
    }
    return str;
}

ObjectMethod::ObjectMethod(const JavaClass &cls, const std::string &name,
                           const JavaClass &returnType,
                           const std::string &param) : JavaMethod(cls, name, returnType, param) {
}

jobject ObjectMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jobject result = AndroidUtil::getEnv()->CallObjectMethodV(base, mId, lst);
    va_end(lst);
    return result;
}

ObjectArrayMethod::ObjectArrayMethod(const JavaClass &cls, const std::string &name,
                                     const JavaArray &returnType, const std::string &parameters)
        : JavaMethod(cls, name, returnType, parameters) {
}

jobjectArray ObjectArrayMethod::call(jobject base, ...) {
    va_list lst;
    va_start(lst, base);
    jobjectArray result = (jobjectArray) AndroidUtil::getEnv()->CallObjectMethodV(base, mId, lst);
    va_end(lst);
    return result;
}

StaticBooleanMethod::StaticBooleanMethod(const JavaClass &cls, const std::string &name,
                                         const std::string &param)
        : JavaStaticMethod(cls, name, JavaBasicType::Boolean, param) {

}

jboolean StaticBooleanMethod::call(...) {
    va_list lst;
    va_start(lst, this);
    jboolean result = AndroidUtil::getEnv()->CallStaticBooleanMethodV(getJClass(), mId, lst);
    va_end(lst);
    return result;
}

StaticObjectMethod::StaticObjectMethod(const JavaClass &cls, const std::string &name,
                                       const JavaClass &returnType, const std::string &parameters)
        : JavaStaticMethod(cls, name, returnType, parameters) {
}

jobject StaticObjectMethod::call(...) {
    va_list lst;
    va_start(lst, this);
    jobject result = AndroidUtil::getEnv()->CallStaticObjectMethodV(getJClass(), mId, lst);
    va_end(lst);
    return result;
}
