package co.infinum.collar.generator.utils

object PathUtils {

    fun getPackageFromPath(path: String): String {
        return when {
            path.contains("kotlin") -> {
                path.substringAfter("kotlin/").replace("/", ".")
            }
            path.contains("java") -> {
                path.substringAfter("java/").replace("/", ".")
            }
            else -> {
                throw Exception("Output path is invalid")
            }
        }
    }

    fun getOutputFromPath(path: String): String {
        return when {
            path.contains("kotlin") -> {
                path.substringBefore("/kotlin") + "/kotlin"
            }
            path.contains("java") -> {
                path.substringBefore("/java") + "/java"
            }
            else -> {
                throw Exception("Output path is invalid")
            }
        }
    }
}