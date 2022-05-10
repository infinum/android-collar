package com.infinum.collar.processor.options

internal interface Options {

    fun maxCount(): Int = throw UnsupportedOperationException()

    fun maxNameSize(): Int = throw UnsupportedOperationException()

    fun nameRegex(): String = throw UnsupportedOperationException()

    fun maxParametersCount(): Int = throw UnsupportedOperationException()

    fun reservedPrefixes(): List<String> = throw UnsupportedOperationException()

    fun reserved(): List<String> = throw UnsupportedOperationException()
}
