// author : newbiechen
// date : 2019-09-19 13:50
// description : 对 C++ 调用 Java 实现一套基础类型
//

#ifndef NBREADER_JNIENVELOPE_H
#define NBREADER_JNIENVELOPE_H

#include <string>
#include <jni.h>

/**
 * Java 类型
 */
class JavaType {
public:
    // JavaType 类型对应的 JNI 签名
    virtual std::string signature() const = 0;

protected:
    JavaType();

    virtual ~JavaType();

private:
    JavaType(const JavaType &);

    const JavaType &operator=(const JavaType &);
};

class JavaArray : public JavaType {

public:
    JavaArray(const JavaType &base);

    std::string signature() const;

private:
    const JavaType &mType;
};


/**
 * Java 基础类型
 */
class JavaBasicType : public JavaType {
public:
    static const JavaBasicType Void;
    static const JavaBasicType Int;
    static const JavaBasicType Long;
    static const JavaBasicType Float;
    static const JavaBasicType Double;
    static const JavaBasicType Boolean;

    JavaBasicType(const std::string &signature);

    std::string signature() const;

private:
    const std::string mSignature;
};

inline JavaBasicType::JavaBasicType(const std::string &signature) : mSignature(signature) {}

inline std::string JavaBasicType::signature() const {
    return mSignature;
}

/**
 * 将 c++ 层的 string 转换成 jstring
 */
class JavaString {

public:
    JavaString(JNIEnv *env, const std::string &str, bool emptyIsNull = true);

    jstring getJString();

    ~JavaString();

private:
    JNIEnv *mEnv;
    jstring mJString;

private:
    JavaString(const JavaString &);

    const JavaString &operator=(const JavaString &);
};

/**
 * Java 类
 */
class JavaClass : public JavaType {

public:
    JavaClass(const std::string &name);

    ~JavaClass();

    jclass getJClass() const;

    std::string signature() const;

private:
    const std::string mName;
    mutable jclass mJClass;

    friend class JavaAny;
};


/**
 * 持有 class 的 Java 类
 */
class JavaAny {
public:
    virtual ~JavaAny();

protected:
    JavaAny(const JavaClass &cls);

    jclass getJClass() const;

private:
    const JavaClass &mClass;

    JavaAny(const JavaAny &);

    const JavaAny &operator=(const JavaAny &);
};

inline jclass JavaAny::getJClass() const {
    return mClass.getJClass();
}

/**
 * Java 构造方法
 */
class JavaConstructor : public JavaAny {
public:
    JavaConstructor(const JavaClass &cls, const std::string &param);

    jobject call(...);

private:
    // 构造方法的 id
    jmethodID mId;
};

/**
 * Java 参数类型
 */
class JavaField : public JavaAny {
public:
    JavaField(const JavaClass &cls, const std::string &name, const JavaType &type);

    virtual ~JavaField();

protected:
    // 参数名
    const std::string mName;
    // 参数 id
    jfieldID mId;
};

/**
 * Java 方法
 */
class JavaMethod : public JavaAny {
public:
    JavaMethod(const JavaClass &cls, const std::string &name, const JavaType &returnType,
               const std::string &param);

    virtual ~JavaMethod();

protected:
    const std::string mName;
    jmethodID mId;
};

/**
 * Java 静态方法
 */
class JavaStaticMethod : public JavaAny {
public:
    JavaStaticMethod(const JavaClass &cls, const std::string &name, const JavaType &returnType,
                     const std::string &param);

    virtual ~JavaStaticMethod();

protected:
    const std::string mName;
    jmethodID mId;
};

/**
 * 基础 Java 类型封装
 */

class ObjectField : public JavaField {
public:
    ObjectField(const JavaClass &cls, const std::string &name, const JavaType &type);

    jobject value(jobject obj) const;
};

/**
 * 基础方法封装
 */

class VoidMethod : public JavaMethod {
public:
    VoidMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    void call(jobject base, ...);
};

class IntMethod : public JavaMethod {

public:
    IntMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    jint call(jobject base, ...);
};

class LongMethod : public JavaMethod {

public:
    LongMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    jlong call(jobject base, ...);
};

class BooleanMethod : public JavaMethod {

public:
    BooleanMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    jboolean call(jobject base, ...);
};

class StringMethod : public JavaMethod {

public:
    StringMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    jstring callForJavaString(jobject base, ...);

    std::string callForCppString(jobject base, ...);
};

class ObjectMethod : public JavaMethod {

public:
    ObjectMethod(const JavaClass &cls, const std::string &name, const JavaClass &returnType,
                 const std::string &param);

    jobject call(jobject base, ...);
};

class ObjectArrayMethod : public JavaMethod {

public:
    ObjectArrayMethod(const JavaClass &cls, const std::string &name, const JavaArray &returnType,
                      const std::string &param);

    jobjectArray call(jobject base, ...);
};

class StaticBooleanMethod : public JavaStaticMethod {
public:
    StaticBooleanMethod(const JavaClass &cls, const std::string &name, const std::string &param);

    jboolean call(...);
};

class StaticObjectMethod : public JavaStaticMethod {

public:
    StaticObjectMethod(const JavaClass &cls, const std::string &name, const JavaClass &returnType,
                       const std::string &param);

    jobject call(...);
};

#endif //NBREADER_JNIENVELOPE_H
