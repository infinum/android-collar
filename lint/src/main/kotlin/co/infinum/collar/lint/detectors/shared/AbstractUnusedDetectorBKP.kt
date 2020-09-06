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