package co.infinum.collar.lint.detectors

import co.infinum.collar.lint.detectors.shared.AbstractUnusedDetector
import co.infinum.collar.lint.issues.UnusedUserPropertyIssue
import co.infinum.collar.lint.issues.shared.AbstractIssue

class UnusedUserPropertiesDetector : AbstractUnusedDetector() {

    override fun annotation(): String = "co.infinum.collar.annotations.UserProperties"

    override fun issue(): AbstractIssue = UnusedUserPropertyIssue()
}
