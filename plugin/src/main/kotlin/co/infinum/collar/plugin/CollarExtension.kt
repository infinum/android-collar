package co.infinum.collar.plugin

open class CollarExtension {

    open var version = "1.1.0"
    open var extended = true
    open var filePath = ""
    open var module = "main"
    open var packageName = ""
}

fun CollarExtension.checkIfValid(): List<String> {
    val errors = mutableListOf<String>()

    if (filePath.isBlank()) {
        errors.add("filePath should be specified")
    }

    if (module.isBlank()) {
        errors.add("module should be specified")
    }

    if (packageName.isBlank()) {
        errors.add("packageId should be specified")
    }

    return errors.toList()
}
