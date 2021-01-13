package co.infinum.collar.plugin

public open class CollarExtension {

    public companion object {
        public const val NAME: String = "collar"

        public const val DEFAULT_VERSION: String = "1.2.0"
        private const val DEFAULT_FILENAME = ""
        private const val DEFAULT_PACKAGE_NAME = ""
    }

    public open var version: String = DEFAULT_VERSION
    public open var fileName: String = DEFAULT_FILENAME
    public open var packageName: String = DEFAULT_PACKAGE_NAME
}

internal fun CollarExtension.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (packageName.isBlank()) {
        errors.add("packageName must be specified")
    }

    return errors.toList()
}
