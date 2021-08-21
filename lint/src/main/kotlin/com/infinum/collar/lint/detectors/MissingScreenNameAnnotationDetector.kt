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
            "androidx.activity.ComponentActivity",
            "androidx.core.app.ComponentActivity",
            "androidx.appcompat.app.AppCompatActivity",
            "androidx.fragment.app.FragmentActivity",
            "android.support.v7.app.AppCompatActivity",
            "android.support.v4.app.FragmentActivity",
            "androidx.fragment.app.Fragment",
            "android.support.v4.app.Fragment"
        )
        private const val ANNOTATION_SCREEN_NAME = "com.infinum.collar.annotations.ScreenName"
    }

    override fun applicableSuperClasses(): List<String> = SUPPORTED_CLASSES

    override fun visitClass(context: JavaContext, declaration: UClass) {
        if (context.project.reportIssues) {
            SUPPORTED_CLASSES.forEach {
                if (context.evaluator.extendsClass(declaration, it, false)) {
                    if (declaration.hasAnnotation(ANNOTATION_SCREEN_NAME).not()) {
                        context.report(
                            IssueFactory.MISSING_SCREEN_NAME_ANNOTATION,
                            declaration,
                            context.getNameLocation(declaration),
                            "Missing ScreenName annotation."
                        )
                    }
                }
            }
        }
    }
}
