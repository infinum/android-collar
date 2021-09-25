package com.infinum.collar.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.symbol.Variance
import com.google.devtools.ksp.validate
import com.infinum.collar.processor.collectors.AnalyticsEventsCollector
import com.infinum.collar.processor.collectors.ScreenNamesCollector
import com.infinum.collar.processor.collectors.UserPropertiesCollector
import com.infinum.collar.processor.extensions.asClassName
import com.infinum.collar.processor.models.ScreenHolder
import com.infinum.collar.processor.subprocessors.AnalyticsEventsSubprocessor
import com.infinum.collar.processor.subprocessors.ScreenNamesSubprocessor
import com.infinum.collar.processor.subprocessors.UserPropertiesSubprocessor
import com.squareup.kotlinpoet.ClassName
import java.io.OutputStream
import javax.lang.model.element.TypeElement

internal class CollarKspProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    private val screenNamesSubprocessor: ScreenNamesSubprocessor = ScreenNamesSubprocessor()
    private val analyticsEventsSubprocessor: AnalyticsEventsSubprocessor = AnalyticsEventsSubprocessor()
    private val userPropertiesSubprocessor: UserPropertiesSubprocessor = UserPropertiesSubprocessor()

    override fun process(resolver: Resolver): List<KSAnnotated> {
//            screenNamesSubprocessor.process(roundEnv)
//            analyticsEventsSubprocessor.process(roundEnv)
//            userPropertiesSubprocessor.process(roundEnv)
        setOf(
            ScreenNamesCollector.SUPPORTED,
            AnalyticsEventsCollector.SUPPORTED,
            UserPropertiesCollector.SUPPORTED
        )
            .flatten()
            .toMutableSet()

        val symbols = resolver
            .getSymbolsWithAnnotation("com.infinum.collar.annotations.ScreenName")
            .filterIsInstance<KSClassDeclaration>()

        if (!symbols.iterator().hasNext()) return emptyList()

        val file: OutputStream = environment.codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = "com.infinum.collar",
            fileName = "ScreenNames"
        )
        file += "package com.infinum.collar\n"

        symbols.forEach { it.accept(Visitor(), Unit) }

        file.close()
        return symbols.filterNot { it.validate() }.toList()
    }

    class Provider : SymbolProcessorProvider {

        override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
            CollarKspProcessor(environment)
    }

    inner class Visitor : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if (classDeclaration.classKind != ClassKind.CLASS) {
                environment.logger.error("Only class can be annotated with @Function", classDeclaration)
                return
            }

            val annotation: KSAnnotation = classDeclaration.annotations.first {
                it.shortName.asString() == "ScreenName"
            }

            val enabledArgument: KSValueArgument? = annotation.arguments
                .firstOrNull { arg -> arg.name?.asString() == "enabled" }

            val valueArgument: KSValueArgument? = annotation.arguments
                .firstOrNull { arg -> arg.name?.asString() == "value" }

            val enabledValue = (enabledArgument?.value as? Boolean) ?: true
            val valueValue = (valueArgument?.value as? String)?.takeIf { it.isNotEmpty() } ?: classDeclaration.simpleName.asString()

            val holder = ScreenHolder(
                enabled = enabledValue,
                superClassName = null,
                className = ClassName(classDeclaration.packageName.asString(), classDeclaration.simpleName.asString()),
                screenName = valueValue
            )

            println(holder)

//            // Getting the list of member properties of the annotated interface.
//            val properties: Sequence<KSPropertyDeclaration> = classDeclaration.getAllProperties()
//                .filter { it.validate() }

//            // Generating function signature.
//            file += "\n"
//            if (properties.iterator().hasNext()) {
//                file += "fun $functionName(\n"
//
//                // Iterating through each property to translate them to function arguments.
//                properties.forEach { prop ->
//                    visitPropertyDeclaration(prop, Unit)
//                }
//                file += ") {\n"
//
//            } else {
//                // Otherwise, generating function with no args.
//                file += "fun $functionName() {\n"
//            }
//
//            // Generating function body
//            file += "    println(\"Hello from $functionName\")\n"
//            file += "}\n"
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) = Unit

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) = Unit
    }
}

internal operator fun OutputStream.plusAssign(str: String) {
    this.write(str.toByteArray())
}
