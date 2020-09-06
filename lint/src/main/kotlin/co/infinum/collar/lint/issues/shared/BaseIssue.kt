package co.infinum.collar.lint.issues.shared

import com.android.tools.lint.detector.api.Issue

interface BaseIssue {

    @Suppress("UnstableApiUsage")
    operator fun invoke(): Issue
}