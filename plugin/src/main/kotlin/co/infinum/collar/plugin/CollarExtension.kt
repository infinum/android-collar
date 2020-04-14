package co.infinum.collar.plugin

open class CollarExtension {

    open var version = "1.1.2"
    open var fileName = ""
    open var variant = "main"
    open var packageName = ""
}

fun CollarExtension.checkIfValid(): List<String> {
    val errors = mutableListOf<String>()

    if (fileName.isBlank()) {
        errors.add("fileName must be specified")
    }

    if (variant.isBlank()) {
        errors.add("variant must be specified")
    }

    if (packageName.isBlank()) {
        errors.add("packageName must be specified")
    }

    return errors.toList()
}
