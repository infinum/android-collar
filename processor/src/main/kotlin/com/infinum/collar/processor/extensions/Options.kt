package com.infinum.collar.processor.extensions

import com.infinum.collar.processor.options.Options

internal fun Options.allReservedKeywords(): String =
    this.reservedPrefixes()
        .plus(this.reserved())
        .joinToString { "\"$it\"" }
