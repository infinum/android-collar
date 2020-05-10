package co.infinum.collar.processor.extensions

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

internal fun Messager.showWarning(message: String) = this.printMessage(Diagnostic.Kind.WARNING, message)

internal fun Messager.showError(message: String) = this.printMessage(Diagnostic.Kind.ERROR, message)
