package co.infinum.generator.utils

import java.nio.file.Files
import java.nio.file.Paths

object FileUtils {

    fun readFromFile(filename: String): String {
        return String(Files.readAllBytes(Paths.get(filename)), Charsets.UTF_8)
    }
}