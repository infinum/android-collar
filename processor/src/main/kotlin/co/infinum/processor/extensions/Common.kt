package co.infinum.processor.extensions

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) this.apply(block) else this
}
