package com.infinum.collar.ui.extensions

internal fun String.redact(keywords: Set<String>): String =
    this.replace(
        Regex(keywords.joinToString("|") { it })
    ) { result -> "".padEnd(result.value.length, '•') }
