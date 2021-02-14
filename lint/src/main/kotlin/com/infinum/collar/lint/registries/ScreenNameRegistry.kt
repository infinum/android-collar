package com.infinum.collar.lint.registries

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue
import com.infinum.collar.lint.issues.Issues.ISSUE_SCREEN_NAME

@Suppress("UnstableApiUsage")
class ScreenNameRegistry : IssueRegistry() {

    override val issues: List<Issue> = listOf(ISSUE_SCREEN_NAME)

    override val api: Int = CURRENT_API
}
