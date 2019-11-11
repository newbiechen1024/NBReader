package com.example.newbiechen.nbreader.uilts

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.util.HashSet

/**
 *  author : newbiechen
 *  date : 2019-11-10 16:25
 *  description :SP 封装类
 */

abstract class NBSharedPrefs {
    private var mMemCached: SharedPreferences? = null

    companion object {
        private val TAG = "FenixRecorderSharedPrefs"
    }

    /**
     * If you want use it in multi-process, change isMemCachingNeeded to false;
     *
     * @return boolean, default is true
     */
    protected val isMemCachingNeeded: Boolean get() = true

    private val sharedPreferences: SharedPreferences?
        get() {
            if (!isMemCachingNeeded) {
                mMemCached = null
                return initSharedPreferences()
            }

            if (mMemCached == null) {
                mMemCached = initSharedPreferences()
            }
            return mMemCached
        }

    /**
     * Sub class should implements this to provider a SharedPreferences Object.
     *
     * @return SharedPreferences @NonNull
     */
    protected abstract fun initSharedPreferences(): SharedPreferences


    fun getString(key: String, defValue: String): String? {
        return sharedPreferences!!.getString(key, defValue)
    }

    fun putString(key: String, value: String) {
        val editor = sharedPreferences!!.edit().putString(key, value)
        editor.apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return sharedPreferences!!.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        val editor = sharedPreferences!!.edit().putInt(key, value)
        editor.apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = sharedPreferences!!.edit().putBoolean(key, value)
        editor.apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return sharedPreferences!!.getLong(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        val editor = sharedPreferences!!.edit().putLong(key, value)
        editor.apply()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return sharedPreferences!!.getFloat(key, defValue)
    }

    fun putFloat(key: String, value: Float) {
        val editor = sharedPreferences!!.edit().putFloat(key, value)
        editor.apply()
    }

    fun getStringSet(key: String, defValue: Set<String>): Set<String>? {
        return sharedPreferences!!.getStringSet(key, defValue)
    }

    fun putStringSet(key: String, value: Set<String>?) {
        var value = value
        if (value == null) {
            // 该库没有进行null的处理
            value = HashSet()
        }
        val editor = sharedPreferences!!.edit().putStringSet(key, value)
        editor.apply()
    }

    fun remove(key: String) {
        val editor = sharedPreferences!!.edit().remove(key)
        editor.apply()
    }

    fun remove(vararg keys: String) {
        val editor = sharedPreferences!!.edit()
        for (key in keys) {
            editor.remove(key)
        }
        editor.apply()
    }

    fun contains(key: String): Boolean {
        return sharedPreferences!!.contains(key)
    }

    fun clear() {
        val editor = sharedPreferences!!.edit().clear()
        editor.apply()
    }
}
