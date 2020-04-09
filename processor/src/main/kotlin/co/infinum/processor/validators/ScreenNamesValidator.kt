package co.infinum.processor.validators

import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.options.Options
import javax.annotation.processing.Messager

class ScreenNamesValidator(
    private val processorOptions: Options,
    private val messager: Messager
) : Validator<ScreenHolder> {

    override fun validate(elements: Set<ScreenHolder>): Set<ScreenHolder> =
        elements.filter { holder ->
            if (holder.enabled) {
                holder.superClassName?.let {
                    val screenName = holder.screenName
                    if (screenName.length > processorOptions.maxNameSize()) {
                        messager.showWarning("Screen names can be up to ${processorOptions.maxNameSize()} characters long. $screenName is ${screenName.length} long.")
                        false
                    } else {
                        true
                    }
                } ?: run {
                    messager.showWarning("${holder.className.simpleName} is not eligible as a screen.")
                    false
                }
            } else {
                false
            }
        }
            .toSet()
}