package co.infinum.collar.plugin

open class CollarExtension {

    open var version = "1.1.1"
    open var extended = true
    open var fileName = ""
    open var variant = "main"
    open var packageName = ""
}

fun CollarExtension.checkIfValid(): List<String> {
    val errors = mutableListOf<String>()

    if (fileName.isBlank()) {
        errors.add("filePath should be specified")
    }

    if (variant.isBlank()) {
        errors.add("module should be specified")
    }

    if (packageName.isBlank()) {
        errors.add("packageId should be specified")
    }

    return errors.toList()
}
