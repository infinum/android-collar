package co.infinum.processor.validators

import co.infinum.processor.models.PropertyHolder
import co.infinum.processor.models.UserPropertiesHolder
import co.infinum.processor.options.Options
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.KotlinMetadata
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Types

class UserPropertiesValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val onWarning: (String) -> Unit,
    private val onError: (String) -> Unit
) : Validator<UserPropertiesHolder> {

    companion object {
        private const val MAX_UNIQUE_PARAMETERS = 1
    }

    override fun verify(element: Element): Boolean =
        isKotlinSealedClass(element.kotlinMetadata, element)

    override fun validate(elements: Set<UserPropertiesHolder>): Set<UserPropertiesHolder> {
        val validRootClasses = elements.filter { verify(it.rootClass) }
        val allPropertiesCount = validRootClasses.flatMap { it.propertyHolders }.count()
        return if (allPropertiesCount > processorOptions.maxCount()) {
            onError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${allPropertiesCount}.")
            setOf()
        } else {
            validRootClasses.map { rootClass ->
                rootClass.copy( propertyHolders = rootClass.propertyHolders.filter { holder ->
                    if (holder.enabled) {
                        if (typeUtils.directSupertypes(holder.type).contains(rootClass.rootClass.asType())) {
                            if (holder.propertyName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                                onWarning("Property name may only contain alphanumeric characters and underscores (\"_\"). ${holder.propertyName} does not.")
                                false
                            } else {
                                if (processorOptions.reserved().any { holder.propertyName.equals(it, false) }) {
                                    onWarning("The ${processorOptions.reserved().joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${holder.propertyName}.")
                                    false
                                } else {
                                    if (holder.propertyParameterNames.isEmpty()) {
                                        onWarning("")
                                        false
                                    } else {
                                        if (holder.propertyParameterNames.size > MAX_UNIQUE_PARAMETERS) {
                                            onWarning("You can associate up to $MAX_UNIQUE_PARAMETERS unique parameter with each user property. Current size is ${holder.propertyParameterNames.size}.")
                                            false
                                        } else {
                                            true
                                        }
                                    }
                                }
                            }
                        } else {
                            onWarning("$holder does not extend from $rootClass.")
                            false
                        }
                    } else {
                        false
                    }
                }.toSet())
            }
                .toSet()
        }
    }

    private fun isKotlinSealedClass(kotlinMetadata: KotlinMetadata?, element: Element): Boolean {
        val check = kotlinMetadata is KotlinClassMetadata && kotlinMetadata.data.classProto.modality == ProtoBuf.Modality.SEALED && element is TypeElement
        if (check.not()) {
            onWarning("$element is not a sealed Kotlin class.")
        }
        return check
    }
}