package co.infinum.collar.lint.detectors

import co.infinum.collar.lint.detectors.shared.AbstractUnusedDetector
import co.infinum.collar.lint.issues.UnusedScreenNameIssue
import co.infinum.collar.lint.issues.shared.AbstractIssue

class UnusedEventsDetector : AbstractUnusedDetector() {

    override fun annotation(): String = "co.infinum.collar.annotations.AnalyticsEvents"

    override fun issue(): AbstractIssue = UnusedScreenNameIssue()
}
