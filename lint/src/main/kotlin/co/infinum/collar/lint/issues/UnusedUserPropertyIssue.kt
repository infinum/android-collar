package co.infinum.collar.lint.issues

import co.infinum.collar.lint.detectors.UnusedUserPropertiesDetector
import co.infinum.collar.lint.issues.shared.AbstractIssue
import com.android.tools.lint.detector.api.Detector

@Suppress("UnstableApiUsage")
class UnusedUserPropertyIssue : AbstractIssue() {

    override fun id(): String = "UnusedUserProperty"

    override fun description(): String = "Unused user properties should be implemented or removed."

    override fun explanation(): String = "All user properties should be implemented or removed from tracking plan."

    override fun detector(): Class<out Detector> = UnusedUserPropertiesDetector::class.java
}
