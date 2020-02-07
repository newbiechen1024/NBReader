// author : newbiechen
// date : 2019-12-08 11:27
// description : 
//

#include "Regex.h"
#include "../Logger.h"

static const std::string TAG = "Regex";

Pattern::Pattern(const std::string &pattern) : mRegex(nullptr) {
    OnigEncoding use_encs[] = {ONIG_ENCODING_UTF8};
    OnigErrorInfo einfo;

    // 初始化正则匹配器
    onig_initialize(use_encs, sizeof(use_encs) / sizeof(use_encs[0]));

    // TODO:可以放到创建的时候初始化
    // UTF-8 匹配
    UChar *regexPattern = (UChar *) pattern.c_str();

    int regexCode = onig_new(&mRegex, regexPattern, regexPattern + pattern.size(),
                             ONIG_OPTION_DEFAULT, ONIG_ENCODING_UTF8, ONIG_SYNTAX_DEFAULT, &einfo);

    if (regexCode != ONIG_NORMAL) {
        char s[ONIG_MAX_ERROR_MESSAGE_LEN];
        onig_error_code_to_str((UChar *) s, regexCode, &einfo);
        Logger::i(TAG, "ERROR:" + std::string(s));

        // TODO:抛出异常
    }
}

Pattern::~Pattern() {
    onig_free(mRegex);
    onig_end();
}

Matcher::Matcher(std::shared_ptr<Pattern> pattern, const char *buffer, size_t bufferLen) : mPattern(
        pattern),
                                                                                           mBuffer(buffer) {

    // 初始化
    mRegion = onig_region_new();
    // 初始化匹配范围
    mStartRange = (const OnigUChar *) mBuffer;

    mFindStartIndex = mFindEndIndex = 0;

    mBufferLen = bufferLen;

    isFindFinish = false;
}

bool Matcher::find() {

    // TODO:1. 处理如果 startRange 到达最终位置时候的情况，即 startRange == endIndex
    // TODO:2. 如果 resultCode == MISMATCH 了就不需要再次匹配查找了

    // 如果匹配已经结束了，就没有必要再 find 了
    if (isFindFinish) {
        return false;
    }

    // 获取起始点和终止点
    const OnigUChar *startIndex = (const OnigUChar *) mBuffer;
    const OnigUChar *endIndex = startIndex + mBufferLen;

    const OnigUChar *endRange = endIndex;

    int resultCode = onig_search(mPattern->mRegex, startIndex, endIndex,
                                 mStartRange, endRange, mRegion, ONIG_OPTION_NONE);

    if (resultCode >= 0) {
        // 匹配成功且存在数据
        if (mRegion->num_regs != 0) {
            
            mFindStartIndex = mRegion->beg[0];
            mFindEndIndex = mRegion->end[0];

            // 将匹配到的结果位置，作为下次匹配起始位置
            mStartRange = startIndex + mRegion->end[0];

            return true;
        } else {
            // 匹配成功没数据，默认为失败
            return false;
        }
    } else {
        if (resultCode == ONIG_MISMATCH) {
            isFindFinish = true;
            // 说明没有匹配到一个

            Logger::i(TAG, "result mismatch");
            return false;
        } else {
            char s[ONIG_MAX_ERROR_MESSAGE_LEN];
            onig_error_code_to_str((UChar *) s, resultCode);
            Logger::i(TAG, "ERROR:" + std::string(s));

            // TODO:需要抛出异常

            isFindFinish = true;
        }
    }

    return false;
}

Matcher::~Matcher() {
    onig_region_free(mRegion, 1 /* 1:free self, 0:free contents only */);
}