package com.example.newbiechen.nbreader.uilts

/**
 *  author : newbiechen
 *  date : 2020-01-18 16:12
 *  description : hashCode 值生成器
 *
 */

class HashCodeCreator {
    private var result = 17

    fun <T> addValue(any: T?): HashCodeCreator {
        if (null == any) {
            return this
        }

        when (any) {
            is Boolean -> {
                any.hashCode()
                incrementResult(if (any) 0 else 1)
            }
            is Byte -> {
                incrementResult(any.toInt())
            }
            is Char -> {
                incrementResult(any.toInt())
            }
            is Short -> {
                incrementResult(any.toInt())
            }
            is Int -> {
                incrementResult(any)
            }
            is Long -> {
                incrementResult((any xor (any ushr 32)).toInt())
            }
            is Float -> {
                incrementResult(java.lang.Float.floatToIntBits(any))
            }
            is Double -> {
                val dValue = java.lang.Double.doubleToLongBits(any)
                incrementResult((dValue xor (dValue ushr 32)).toInt())
            }
            is List<*> -> {
                for (obj in any) {
                    incrementResult(obj.hashCode())
                }
            }
            else -> {
                incrementResult(any.hashCode())
            }
        }
        return this
    }

    private fun incrementResult(value: Int) {
        result = result * HASH_INCREMENT + value
    }

    fun build(): Int {
        val value = result
        result = 0
        return value
    }

    companion object {
        private const val HASH_INCREMENT = 37
        fun create(): HashCodeCreator {
            return HashCodeCreator()
        }
    }
}
