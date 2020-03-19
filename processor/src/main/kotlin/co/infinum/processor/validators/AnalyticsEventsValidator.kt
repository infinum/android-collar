package co.infinum.processor.validators

import co.infinum.processor.collectors.AnalyticsEventsCollector
import co.infinum.processor.options.Options
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

class AnalyticsEventsValidator(
    private val processorOptions: Options,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val collector: AnalyticsEventsCollector,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator {

    override fun verify(element: Element): TypeElement? =
        when (isKotlinSealedClass(element.kotlinMetadata, element)) {
            true -> element as TypeElement
            false -> null
        }

    override fun validate(elements: MutableSet<out Element>): List<Element> {

    }

    private fun isKotlinSealedClass(kotlinMetadata: KotlinMetadata?, element: Element): Boolean =
        kotlinMetadata is KotlinClassMetadata && kotlinMetadata.data.classProto.modality == ProtoBuf.Modality.SEALED && element is TypeElement
}