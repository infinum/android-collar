package co.infinum.collar.plugin

open class CollarExtension {

    companion object {
        const val NAME = "collar"

        private const val DEFAULT_VERSION = "1.1.2"
        private const val DEFAULT_FILEPATH = ""
        private const val DEFAULT_VARIANT = "main"
        private const val DEFAULT_PACKAGENAME = ""
    }

    open var version = DEFAULT_VERSION
    open var filePath = DEFAULT_FILEPATH
    open var variant = DEFAULT_VARIANT
    open var packageName = DEFAULT_PACKAGENAME
}

fun CollarExtension.validate(): List<String> {
    val errors = mutableListOf<String>()

    if (filePath.isBlank()) {
        errors.add("filePath must be specified")
    }

    if (variant.isBlank()) {
        errors.add("variant must be specified")
    }

    if (packageName.isBlank()) {
        errors.add("packageName must be specified")
    }

    return errors.toList()
}
