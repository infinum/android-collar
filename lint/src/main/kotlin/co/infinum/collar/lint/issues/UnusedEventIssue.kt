package co.infinum.collar.lint.issues

import co.infinum.collar.lint.detectors.UnusedEventsDetector
import co.infinum.collar.lint.issues.shared.AbstractIssue
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import java.util.EnumSet

@Suppress("UnstableApiUsage")
class UnusedEventIssue : AbstractIssue() {

    override fun id(): String = "UnusedEvent"

    override fun description(): String = "Unused event should be implemented or removed."

    override fun explanation(): String = "All events should be implemented or removed from the tracking plan."

    override fun detector(): Class<out Detector> = UnusedEventsDetector::class.java
}
