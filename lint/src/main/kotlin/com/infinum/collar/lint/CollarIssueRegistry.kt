package com.infinum.collar.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
public class CollarIssueRegistry : IssueRegistry() {

    override val issues: List<Issue> = IssueFactory.issues()

    override val api: Int = CURRENT_API
}
