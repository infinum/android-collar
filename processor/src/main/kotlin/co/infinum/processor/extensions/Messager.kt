package co.infinum.processor.extensions

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

fun Messager.showWarning(message: String) = this.printMessage(Diagnostic.Kind.WARNING, message)

fun Messager.showError(message: String) = this.printMessage(Diagnostic.Kind.ERROR, message)