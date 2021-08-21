package com.infinum.collar.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
public class CollarIssueRegistry : IssueRegistry() {

    override val vendor: Vendor = Vendor(
        vendorName = "Infinum Inc.",
        identifier = "com.infinum.collar:collar-lint:{version}",
        feedbackUrl = "https://github.com/infinum/android-collar/issues"
    )

    override val issues: List<Issue> = IssueFactory.issues()

    override val api: Int = CURRENT_API
}
