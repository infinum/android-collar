package com.infinum.collar.processor.validators

import com.infinum.collar.processor.extensions.showWarning
import com.infinum.collar.processor.models.ScreenHolder
import com.infinum.collar.processor.options.Options
import javax.annotation.processing.Messager

internal class ScreenNamesValidator(
    private val processorOptions: Options,
    private val messager: Messager
) : Validator<ScreenHolder> {

    override fun validate(elements: Set<ScreenHolder>): Set<ScreenHolder> =
        elements.filter { it.enabled && validateSuperType(it) }.toSet()

    private fun validateSuperType(holder: ScreenHolder): Boolean =
        holder.superClassName?.let {
            validateNameLength(holder)
        } ?: run {
            messager.showWarning("${holder.className.simpleName} is not eligible as a screen.")
            false
        }

    private fun validateNameLength(holder: ScreenHolder): Boolean =
        if (holder.screenName.length > processorOptions.maxNameSize()) {
            messager.showWarning(
                "Screen names can be up to ${processorOptions.maxNameSize()} characters long. " +
                    "${holder.screenName} is ${holder.screenName.length} long."
            )
            false
        } else {
            true
        }
}
