package com.infinum.collar.processor.extensions

internal inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}

internal inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) this.apply(block) else this
}
