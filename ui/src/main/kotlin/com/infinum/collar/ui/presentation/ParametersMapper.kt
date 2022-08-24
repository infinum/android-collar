package com.infinum.collar.ui.presentation

import android.os.Bundle
import com.infinum.collar.ui.extensions.redact
import timber.log.Timber

@Suppress("ComplexMethod")
internal object ParametersMapper {

    @Suppress("NestedBlockDepth")
    fun toRedactedString(parameterMap: Map<String, *>, redactedKeywords: Set<String>): String {
        val map = mutableMapOf<String, String>()

        val keys: Set<String> = parameterMap.keys

        val iterator = keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = parameterMap[key]

            map[key] = value?.let {
                when (it) {
                    is String -> parameterMap[key].toString()
                    is Boolean -> parameterMap[key].toString()
                    is Byte -> parameterMap[key].toString()
                    is Char -> parameterMap[key].toString()
                    is Double -> parameterMap[key].toString()
                    is Float -> parameterMap[key].toString()
                    is Int -> parameterMap[key].toString()
                    is Long -> parameterMap[key].toString()
                    is Short -> parameterMap[key].toString()
                    is CharSequence -> parameterMap[key].toString()
                    is Map<*, *> -> {
                        @Suppress("UNCHECKED_CAST")
                        (parameterMap[key] as? Map<String, *>)?.let { childMap ->
                            toRedactedString(childMap, redactedKeywords)
                        }
                    }
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
