package com.infinum.collar.plugin

public open class CollarExtension {

    public companion object {
        public const val NAME: String = "collar"

        public const val DEFAULT_VERSION: String = "1.2.1"
        private const val DEFAULT_FILENAME = ""
        private const val DEFAULT_PACKAGE_NAME = ""
    }

    public open var version: String = DEFAULT_VERSION
    public open var fileName: String = DEFAULT_FILENAME
    public open var packageName: String = DEFAULT_PACKAGE_NAME
}

internal fun CollarExtension.validate(): List<String> =
    when {
        packageName.isBlank() -> listOf("packageName must be specified")
        else -> listOf()
    }
