package co.infinum.processor.validators

import co.infinum.processor.extensions.isSealedClass
import co.infinum.processor.extensions.showError
import co.infinum.processor.extensions.showWarning
import co.infinum.processor.models.UserPropertiesHolder
import co.infinum.processor.options.Options
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.Messager
import javax.lang.model.util.Types

class UserPropertiesValidator(
    private val processorOptions: Options,
    private val typeUtils: Types,
    private val messager: Messager
) : Validator<UserPropertiesHolder> {

    companion object {
        private const val MAX_UNIQUE_PARAMETERS = 1
    }

    @KotlinPoetMetadataPreview
    override fun validate(elements: Set<UserPropertiesHolder>): Set<UserPropertiesHolder> {
        val validRootClasses = elements.filter {
            if (it.rootClass.isSealedClass().not()) {
                messager.showWarning("$it is not a sealed Kotlin class.")
                false
            } else {
                true
            }
        }
        val innerClassesCount = validRootClasses.flatMap { it.propertyHolders }.count()
        return if (innerClassesCount > processorOptions.maxCount()) {
            messager.showError("You can report up to ${processorOptions.maxCount()} different user properties per app. Current size is ${innerClassesCount}.")
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
                                if (holder.propertyName.first().isLetter().not()) {
                                    messager.showWarning("User property name must start with an alphabetic character. ${holder.propertyName.first()} in ${holder.propertyName} is not a letter.")
                                    false
                                } else {
                                    if (processorOptions.reservedPrefixes().any { holder.propertyName.startsWith(it, false) } || processorOptions.reserved().any { holder.propertyName.equals(it, false) }) {
                                        messager.showWarning("The ${processorOptions.reservedPrefixes().plus(processorOptions.reserved()).joinToString { "\"$it\"" }} user properties are reserved and cannot be used as in ${holder.propertyName}.")
                                        false
                                    } else {
                                        if (holder.propertyParameterNames.isEmpty()) {
                                            messager.showWarning("")
                                            false
                                        } else {
                                            if (holder.propertyParameterNames.size > processorOptions.maxParametersCount()) {
                                                messager.showWarning("You can associate up to ${processorOptions.maxParametersCount()} unique parameter with each user property. Current size is ${holder.propertyParameterNames.size}.")
                                                false
                                            } else {
                                                true
                                            }
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
            }.toSet()
        }
    }
}