package co.infinum.processor.extensions

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}