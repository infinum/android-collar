package co.infinum.collar.plugin

internal open class CollarExtension {

    companion object {
        const val NAME = "collar"

        const val DEFAULT_VERSION = "1.1.8"
        private const val DEFAULT_FILENAME = ""
        private const val DEFAULT_PACKAGE_NAME = ""
    }

    open var version = DEFAULT_VERSION
    open var fileName = DEFAULT_FILENAME
    open var packageName = DEFAULT_PACKAGE_NAME
}

internal fun CollarExtension.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (packageName.isBlank()) {
        errors.add("packageName must be specified")
    }

    return errors.toList()
}
