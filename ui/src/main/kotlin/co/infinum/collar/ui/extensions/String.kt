package co.infinum.collar.ui.extensions

fun String.redact(keywords: Set<String>): String =
    this.replace(
        Regex(keywords.joinToString("|") { it })
    ) { result -> "".padEnd(result.value.length, '•') }
