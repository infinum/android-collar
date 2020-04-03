package co.infinum.collar.generator.utils

import java.io.File

object FileUtils {

    fun readFromFile(fileName: String): String {
        return File(fileName).readText(Charsets.UTF_8)
    }
}