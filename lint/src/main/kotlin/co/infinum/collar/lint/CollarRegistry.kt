package co.infinum.collar.lint

import co.infinum.collar.lint.detectors.shared.AbstractUnusedDetector
import co.infinum.collar.lint.issues.UnusedEventIssue
import co.infinum.collar.lint.issues.UnusedScreenNameIssue
import co.infinum.collar.lint.issues.UnusedUserPropertyIssue
import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
class CollarRegistry : IssueRegistry() {

    companion object {

        private val ENABLED_ISSUES = listOf(
            UnusedScreenNameIssue(),
            UnusedEventIssue(),
            UnusedUserPropertyIssue()
        )
    }

    override val issues: List<Issue> = ENABLED_ISSUES.map { it.invoke() }
        .plus(AbstractUnusedDetector.INHERITANCE_USAGE)
        .plus(AbstractUnusedDetector.TYPE_USAGE)

    override val api: Int = CURRENT_API
}
