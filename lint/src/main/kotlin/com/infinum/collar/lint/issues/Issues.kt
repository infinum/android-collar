package com.infinum.collar.lint.issues

import com.infinum.collar.lint.detectors.ScreenNameDetector
import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import java.util.EnumSet

@Suppress("UnstableApiUsage")
object Issues {

    val ISSUE_SCREEN_NAME = Issue.create(
        id = "MissingScreenNameAnnotation",
        briefDescription = "Missing screen name annotation",
        explanation = "All Activities and Fragments require a valid screen name annotation on the class." +
            "\nYou must use at @ScreenName with a valid and provided String value.",
        category = CORRECTNESS,
        priority = 5,
        severity = Severity.WARNING,
        implementation = Implementation(ScreenNameDetector::class.java, EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES))
    )
}
