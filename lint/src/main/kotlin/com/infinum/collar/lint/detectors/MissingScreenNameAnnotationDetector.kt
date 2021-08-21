package com.infinum.collar.lint.detectors

import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.infinum.collar.lint.IssueFactory
import org.jetbrains.uast.UClass

@Suppress("UnstableApiUsage")
internal class MissingScreenNameAnnotationDetector : Detector(), SourceCodeScanner {

    companion object {
        private val SUPPORTED_CLASSES = listOf(
            "android.app.Activity",
            "android.app.Fragment",
            "androidx.fragment.app.Fragment",
            "android.support.v4.app.Fragment"
        )
        private const val ANNOTATION_SCREEN_NAME = "com.infinum.collar.annotations.ScreenName"
    }

    override fun applicableSuperClasses(): List<String> = SUPPORTED_CLASSES

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (context.project.reportIssues) {
            visitExtendedClass(context, declaration)
        }
    }

    private fun visitExtendedClass(context: JavaContext, declaration: UClass) {
        loop@ for (className in SUPPORTED_CLASSES) {
            if (context.evaluator.extendsClass(declaration, className, false)) {
                if (declaration.hasAnnotation(ANNOTATION_SCREEN_NAME).not()) {
                    context.report(
                        IssueFactory.MISSING_SCREEN_NAME_ANNOTATION,
                        declaration,
                        context.getNameLocation(declaration),
                        "Missing screen name annotation."
                    )
                    break@loop
                }
            }
        }
    }
}
