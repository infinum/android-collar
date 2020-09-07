package co.infinum.collar.lint.detectors.shared

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.intellij.psi.PsiJavaCodeReferenceElement
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.toUElement

@Suppress("UnstableApiUsage")
abstract class AbstractUnusedDetector : AbstractDetector() {

    override fun quickFix(): LintFix? = null

    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitClass(node: UClass) = visitClassImpl(context, node)
        }
    }

    private fun visitClassImpl(context: JavaContext, node: UClass) {
        parentClassOf(node)?.let { parentClass ->
            if (hasAnnotation(parentClass)) {
                report(context, node)
            }
        }
    }

    private fun parentClassOf(node: UClass): PsiJavaCodeReferenceElement? {
        return node.extendsList
            ?.referenceElements
            ?.firstOrNull()
    }

    private fun hasAnnotation(element: PsiJavaCodeReferenceElement): Boolean {
        return element.resolve()
            ?.toUElement(UClass::class.java)
            ?.let {
                hasAnnotation(it)
            }
            ?: false
    }

    private fun hasAnnotation(node: UClass): Boolean {
        return node.annotations
            .any { annotation() == it.qualifiedName }
    }
}

/*


package co.infinum.collar.lint.detectors

import com.android.tools.lint.detector.api.AnnotationUsageType
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.ConstantEvaluator
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.android.tools.lint.detector.api.isKotlin
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UNamedExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.getParentOfType

@Suppress("SyntheticAccessor")
class ExperimentalDetector : Detector(), SourceCodeScanner {

    override fun applicableAnnotations(): List<String>? = listOf(
        JAVA_EXPERIMENTAL_ANNOTATION,
        KOTLIN_EXPERIMENTAL_ANNOTATION
    )

    override fun visitAnnotationUsage(
        context: JavaContext,
        usage: UElement,
        type: AnnotationUsageType,
        annotation: UAnnotation,
        qualifiedName: String,
        method: PsiMethod?,
        referenced: PsiElement?,
        annotations: List<UAnnotation>,
        allMemberAnnotations: List<UAnnotation>,
        allClassAnnotations: List<UAnnotation>,
        allPackageAnnotations: List<UAnnotation>
    ) {
        when (qualifiedName) {
            JAVA_EXPERIMENTAL_ANNOTATION -> {
                checkExperimentalUsage(context, annotation, usage,
                    JAVA_USE_EXPERIMENTAL_ANNOTATION
                )
            }
            KOTLIN_EXPERIMENTAL_ANNOTATION -> {
                if (!isKotlin(usage.sourcePsi)) {
                    checkExperimentalUsage(context, annotation, usage,
                        KOTLIN_USE_EXPERIMENTAL_ANNOTATION
                    )
                }
            }
        }
    }

    /**
     * Check whether the given experimental API [annotation] can be referenced from [usage] call
     * site.
     *
     * @param context the lint scanning context
     * @param annotation the experimental annotation detected on the referenced element
     * @param usage the element whose usage should be checked
     * @param useAnnotationName fully-qualified class name for experimental opt-in annotation
     */
    private fun checkExperimentalUsage(
        context: JavaContext,
        annotation: UAnnotation,
        usage: UElement,
        useAnnotationName: String
    ) {
        val useAnnotation = (annotation.uastParent as? UClass)?.qualifiedName ?: return
        if (!hasOrUsesAnnotation(context, usage, useAnnotation, useAnnotationName)) {
            val level = extractAttribute(annotation, "level")
            if (level != null) {
                report(context, usage, """
                    Bojan, This declaration is experimental and its usage should be marked with
                    '@$useAnnotation' or '@UseExperimental(markerClass = $useAnnotation.class)'
                """, level)
            } else {
                report(context, annotation, """
                    Bojan, Failed to extract attribute "level" from annotation
                """, "ERROR")
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun extractAttribute(annotation: UAnnotation, name: String): String? {
        val expression = annotation.findAttributeValue(name) as? UReferenceExpression
        return (ConstantEvaluator().evaluate(expression) as? PsiField)?.name
    }

    /**
     * Check whether the specified [usage] is either within the scope of [annotationName] or an
     * explicit opt-in via the [useAnnotationName] annotation.
     */
    private fun hasOrUsesAnnotation(
        context: JavaContext,
        usage: UElement,
        annotationName: String,
        useAnnotationName: String
    ): Boolean {
        var element: UAnnotated? = if (usage is UAnnotated) {
            usage
        } else {
            usage.getParentOfType(UAnnotated::class.java)
        }

        while (element != null) {
            val annotations = context.evaluator.getAllAnnotations(element, false)
            val matchName = annotations.any { it.qualifiedName == annotationName }
            val matchUse = annotations
                .filter { annotation -> annotation.qualifiedName == useAnnotationName }
                .mapNotNull { annotation -> annotation.attributeValues.getOrNull(0) }
                .any { attrValue -> attrValue.getFullyQualifiedName(context) == annotationName }
            if (matchName || matchUse) return true
            element = element.getParentOfType(UAnnotated::class.java)
        }
        return false
    }

    /**
     * Reports an issue and trims indentation on the [message].
     */
    private fun report(
        context: JavaContext,
        usage: UElement,
        message: String,
        level: String

    ) {
        val issue = when (level) {
            "ERROR" -> ISSUE_ERROR
            "WARNING" -> ISSUE_WARNING
            else -> throw IllegalArgumentException("Level was \"" + level + "\" but must be one " +
                "of: ERROR, WARNING")
        }
        context.report(issue, usage, context.getNameLocation(usage), message.trimIndent())
    }

    companion object {
        private val IMPLEMENTATION = Implementation(
            ExperimentalDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        private const val KOTLIN_EXPERIMENTAL_ANNOTATION =
//            "kotlin.Experimental"
            "co.infinum.collar.annotations.UserProperties"
        private const val KOTLIN_USE_EXPERIMENTAL_ANNOTATION = "kotlin.UseExperimental"

        private const val JAVA_EXPERIMENTAL_ANNOTATION =
//            "androidx.annotation.experimental.Experimental"
            "co.infinum.collar.annotations.UserProperties"
        private const val JAVA_USE_EXPERIMENTAL_ANNOTATION =
            "androidx.annotation.experimental.UseExperimental"

        @Suppress("DefaultLocale")
        private fun issueForLevel(level: String, severity: Severity): Issue = Issue.create(
            id = "UnsafeExperimentalUsage${level.capitalize()}",
            briefDescription = "Unsafe experimental usage intended to be $level-level severity",
            explanation = """
                This API has been flagged as experimental with $level-level severity.

                Any declaration annotated with this marker is considered part of an unstable API \
                surface and its call sites should accept the experimental aspect of it either by \
                using `@UseExperimental`, or by being annotated with that marker themselves, \
                effectively causing further propagation of that experimental aspect.
            """,
            category = Category.CORRECTNESS,
            priority = 4,
            severity = severity,
            implementation = IMPLEMENTATION
        )

        val ISSUE_ERROR =
            issueForLevel(
                "error",
                Severity.ERROR
            )
        val ISSUE_WARNING =
            issueForLevel(
                "warning",
                Severity.WARNING
            )

        val ISSUES = listOf(
            ISSUE_ERROR,
            ISSUE_WARNING
        )
    }
}

/**
 * Returns the fully-qualified class name for a given attribute value, if any.
 */
private fun UNamedExpression?.getFullyQualifiedName(context: JavaContext): String? {
    val exp = this?.expression
    val type = if (exp is UClassLiteralExpression) exp.type else exp?.evaluate()
    return (type as? PsiClassType)?.let { context.evaluator.getQualifiedName(it) }
}

 */


/*

package co.infinum.collar.lint.detectors.shared

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.AnnotationUsageType
import com.android.tools.lint.detector.api.ConstantEvaluator
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotated
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UClassLiteralExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UNamedExpression
import org.jetbrains.uast.UReferenceExpression
import org.jetbrains.uast.UVariable
import org.jetbrains.uast.getParentOfType
import org.jetbrains.uast.visitor.AbstractUastVisitor

@Suppress("UnstableApiUsage")
abstract class AbstractUnusedDetectorBKP : AbstractDetector() {

    override fun applicableAnnotations(): List<String>? =
        listOfNotNull(annotation()).takeIf { it.isNullOrEmpty().not() }

    override fun quickFix(): LintFix? = null

    override fun visitAnnotationUsage(
        context: JavaContext,
        usage: UElement,
        type: AnnotationUsageType,
        annotation: UAnnotation,
        qualifiedName: String,
        method: PsiMethod?,
        referenced: PsiElement?,
        annotations: List<UAnnotation>,
        allMemberAnnotations: List<UAnnotation>,
        allClassAnnotations: List<UAnnotation>,
        allPackageAnnotations: List<UAnnotation>
    ) {
        if (context.project.reportIssues) {
            if (qualifiedName == annotation()) {
                val useAnnotation = (annotation.uastParent as? UClass)?.qualifiedName ?: return
                if (!hasOrUsesAnnotation(context, usage, useAnnotation, annotation()!!)) {
                    (usage as? UClass)
                        ?.innerClasses
                        ?.forEach {
                            report(context, it)
                        }
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun extractAttribute(annotation: UAnnotation, name: String): String? {
        val expression = annotation.findAttributeValue(name) as? UReferenceExpression
        return (ConstantEvaluator().evaluate(expression) as? PsiField)?.name
    }

    private fun hasOrUsesAnnotation(
        context: JavaContext,
        usage: UElement,
        annotationName: String,
        useAnnotationName: String
    ): Boolean {
        var element: UAnnotated? = if (usage is UAnnotated) {
            usage
        } else {
            usage.getParentOfType(UAnnotated::class.java)
        }

        while (element != null) {
            val annotations = context.evaluator.getAllAnnotations(element, false)
            val matchName = annotations.any { it.qualifiedName == annotationName }
            val matchUse = annotations
                .filter { annotation -> annotation.qualifiedName == useAnnotationName }
                .mapNotNull { annotation -> annotation.attributeValues.getOrNull(0) }
                .any { attrValue -> attrValue.getFullyQualifiedName(context) == annotationName }
            if (matchName || matchUse) return true
            element = element.getParentOfType(UAnnotated::class.java)
        }
        return false
    }

    private fun UNamedExpression?.getFullyQualifiedName(context: JavaContext): String? {
        val exp = this?.expression
        val type = if (exp is UClassLiteralExpression) exp.type else exp?.evaluate()
        return (type as? PsiClassType)?.let { context.evaluator.getQualifiedName(it) }
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? =
        listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) =
        object : UElementHandler() {

            override fun visitClass(node: UClass) {
                node.accept(UnusedVisitor(context))
            }
        }

    inner class UnusedVisitor(
        private val context: JavaContext
    ) : AbstractUastVisitor() {

        override fun afterVisitAnnotation(node: UAnnotation) {
            if (context.project.reportIssues) {
                if (node.qualifiedName == annotation()) {
                    val usage = node.uastParent as? UClass
                    usage?.let { usageClass ->
                        usageClass.innerClasses
                            .forEach {
                                report(context, it)
                            }
                    }
                }
            }
        }

        override fun visitAnnotation(node: UAnnotation): Boolean {
            if (context.project.reportIssues) {
                if (node.qualifiedName == annotation()) {
                    val usage = node.uastParent as? UClass
                    usage?.let { usageClass ->
                        usageClass.innerClasses
                            .forEach {
                                report(context, it)
                            }
                    }
                }
            }
            return true
        }

//        override fun visitAnnotation(node: UAnnotation) {
//            if (context.project.reportIssues) {
//                if (node.qualifiedName == annotation()) {
//                    val usage = node.uastParent as? UClass
//                    usage?.let { usageClass ->
//                        usageClass.innerClasses
//                            .forEach {
//                                report(context, it)
//                            }
//                    }
//                }
//            }
//        }
    }
}

 */