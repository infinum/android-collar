package co.infinum.processor.validators

import co.infinum.processor.models.ScreenHolder
import co.infinum.processor.options.Options

class ScreenNamesValidator(
    private val processorOptions: Options,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator<ScreenHolder> {

    override fun validate(elements: Set<ScreenHolder>): Set<ScreenHolder> =
        elements
            .filter { it.enabled }
            .filter { holder ->
                holder.superClassName?.let {
                    val screenName = holder.screenName
                    if (screenName.length > processorOptions.maxNameSize()) {
                        onWarning("Screen names can be up to ${processorOptions.maxNameSize()} characters long. $screenName is ${screenName.length} long.")
                        false
                    } else {
                        true
                    }
                } ?: run {
                    onError("${holder.className.simpleName} is not eligible as a screen.")
                    false
                }
            }
            .toSet()
}