package co.infinum.collar.lint

import co.infinum.collar.lint.issues.UnusedEventIssue
import co.infinum.collar.lint.issues.UnusedScreenNameIssue
import co.infinum.collar.lint.issues.UnusedUserPropertyIssue
import co.infinum.collar.lint.issues.shared.AbstractIssue
import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

@Suppress("UnstableApiUsage")
class CollarRegistry : IssueRegistry() {

    companion object {

        private val ENABLED_ISSUES: List<AbstractIssue> = listOf(
            UnusedScreenNameIssue(),
            UnusedEventIssue(),
            UnusedUserPropertyIssue()
        )
    }

    override val issues: List<Issue> = ENABLED_ISSUES.takeIf { it.isNotEmpty() }.orEmpty().map { it.invoke() }

    override val api: Int = CURRENT_API
}
