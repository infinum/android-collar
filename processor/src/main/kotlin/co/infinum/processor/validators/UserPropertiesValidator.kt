package co.infinum.processor.validators

import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.UserPropertiesHolder
import co.infinum.processor.options.Options
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isClass
import com.squareup.kotlinpoet.metadata.isSealed
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.util.Types

@KotlinPoetMetadataPreview
class UserPropertiesValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val messager: Messager
) : Validator<UserPropertiesHolder> {

    companion object {
        private const val MAX_UNIQUE_PARAMETERS = 1
    }

    override fun verify(element: Element) =
        isKotlinSealedClass(element)

    override fun validate(elements: Set<UserPropertiesHolder>): Set<UserPropertiesHolder> {
        val validRootClasses = elements.filter { verify(it.rootClass) }
        val allPropertiesCount = validRootClasses.flatMap { it.propertyHolders }.count()
        return if (allPropertiesCount > processorOptions.maxCount()) {
            messager.showError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${allPropertiesCount}.")
            setOf()
        } else {
            validRootClasses.map { rootClass ->
                rootClass.copy(propertyHolders = rootClass.propertyHolders.filter { holder ->
                    if (holder.enabled) {
                        if (typeUtils.directSupertypes(holder.type).contains(rootClass.rootClass.asType())) {
                            if (holder.propertyName.matches(Regex("^[a-zA-Z0-9_]*$")).not()) {
                                messager.showWarning("Property name may only contain alphanumeric characters and underscores (\"_\"). ${holder.propertyName} does not.")
                                false
                            } else {
                                if (processorOptions.reserved().any { holder.propertyName.equals(it, false) }) {
                                    messager.showWarning("The ${processorOptions.reserved().joinToString { "\"$it\"" }} user properties are reserved and cannot be used in ${holder.propertyName}.")
                                    false
                                } else {
                                    if (holder.propertyParameterNames.isEmpty()) {
                                        messager.showWarning("")
                                        false
                                    } else {
                                        if (holder.propertyParameterNames.size > MAX_UNIQUE_PARAMETERS) {
                                            messager.showWarning("You can associate up to $MAX_UNIQUE_PARAMETERS unique parameter with each user property. Current size is ${holder.propertyParameterNames.size}.")
                                            false
                                        } else {
                                            true
                                        }
                                    }
                                }
                            }
                        } else {
                            messager.showWarning("$holder does not extend from $rootClass.")
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

    private fun isKotlinSealedClass(element: Element): Boolean {
        val typeMetadata = element.getAnnotation(Metadata::class.java).toImmutableKmClass()
        val check = typeMetadata.isClass && typeMetadata.isSealed
        if (check.not()) {
            messager.showWarning("$element is not a sealed Kotlin class.")
        }
        return check
    }
}