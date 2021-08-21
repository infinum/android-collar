package com.infinum.collar.ui.presentation

import android.os.Bundle
import com.infinum.collar.ui.extensions.redact
import timber.log.Timber

@Suppress("ComplexMethod")
internal object BundleMapper {

    fun toMap(bundle: Bundle, redactedKeywords: Set<String>): String {
        val map = mutableMapOf<String, String>()

        val keys: Set<String> = bundle.keySet()

        val iterator = keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = bundle.get(key)
            map[key] = value?.let {
                when (value) {
                    is String -> bundle.getString(key)
                    is Boolean -> bundle.getBoolean(key).toString()
                    is Byte -> bundle.getByte(key).toString()
                    is Char -> bundle.getChar(key).toString()
                    is Double -> bundle.getDouble(key).toString()
                    is Float -> bundle.getFloat(key).toString()
                    is Int -> bundle.getInt(key).toString()
                    is Long -> bundle.getLong(key).toString()
                    is Short -> bundle.getShort(key).toString()
                    is CharSequence -> bundle.getCharSequence(key).toString()
                    is Bundle -> toMap(
                        bundle.getBundle(key) ?: Bundle.EMPTY,
                        redactedKeywords
                    )
                    else -> {
                        Timber.w(
                            "Illegal value type ${value.javaClass.canonicalName} for key \"$key\""
                        )
                        ""
                    }
                }
            }.orEmpty().redact(redactedKeywords)
        }
        return map.toList().joinToString("\n") { "${it.first} = ${it.second}" }
    }

    fun toBundle(vararg pairs: Pair<String, Any?>) = Bundle(pairs.size).apply {
        for ((key, value) in pairs) {
            when (value) {
                null -> putString(key, null)
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Byte -> putByte(key, value)
                is Char -> putChar(key, value)
                is Double -> putDouble(key, value)
                is Float -> putFloat(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Short -> putShort(key, value)
                is CharSequence -> putCharSequence(key, value)
                is Bundle -> putBundle(key, value)
                else -> Timber.w(
                    "Illegal value type ${value.javaClass.canonicalName} for key \"$key\""
                )
            }
        }
    }
}
