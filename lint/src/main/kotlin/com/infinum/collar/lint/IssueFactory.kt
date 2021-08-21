package com.infinum.collar.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.infinum.collar.lint.detectors.MissingScreenNameAnnotationDetector
import java.util.EnumSet

@Suppress("UnstableApiUsage")
internal object IssueFactory {

    fun issues(): List<Issue> = listOf(
        MISSING_SCREEN_NAME_ANNOTATION
    )

    val MISSING_SCREEN_NAME_ANNOTATION: Issue = Issue.create(
        id = "MissingScreenNameAnnotation",
        briefDescription = "Missing screen name annotation",
        explanation = "All Activities and Fragments require a valid screen name annotation on the class." +
            "\nYou must use annotate this Activity or Fragment with @ScreenName with a valid value parameter or set enabled parameter to false.",
        category = Category.CORRECTNESS,
        priority = 5,
        severity = Severity.WARNING,
        implementation = Implementation(
            MissingScreenNameAnnotationDetector::class.java,
            EnumSet.of(
                Scope.JAVA_FILE,
                Scope.TEST_SOURCES
            )
        )
    )
}
