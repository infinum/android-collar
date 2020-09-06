package co.infinum.collar.lint.detectors

import co.infinum.collar.lint.detectors.shared.AbstractUnusedDetector
import co.infinum.collar.lint.issues.UnusedScreenNameIssue
import co.infinum.collar.lint.issues.shared.AbstractIssue

class UnusedScreenNamesDetector : AbstractUnusedDetector() {

    override fun annotation(): String = "co.infinum.collar.annotations.ScreenNames"

    override fun issue(): AbstractIssue = UnusedScreenNameIssue()
}
