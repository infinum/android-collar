package co.infinum.processor.extensions

import me.eugeniomarletti.kotlin.processing.KotlinAbstractProcessor
import javax.tools.Diagnostic

inline fun consume(action: () -> Unit): Boolean {
    action()
    return true
}

fun KotlinAbstractProcessor.showError(message: String) =
    messager.printMessage(Diagnostic.Kind.ERROR, message)

fun KotlinAbstractProcessor.showWarning(message: String) =
    messager.printMessage(Diagnostic.Kind.WARNING, message)