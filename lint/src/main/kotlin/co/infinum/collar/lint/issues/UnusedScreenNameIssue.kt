package co.infinum.collar.lint.issues

import co.infinum.collar.lint.detectors.UnusedScreenNamesDetector
import co.infinum.collar.lint.issues.shared.AbstractIssue
import co.infinum.collar.lint.issues.shared.BaseIssue
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import java.util.EnumSet

@Suppress("UnstableApiUsage")
class UnusedScreenNameIssue : AbstractIssue() {

    override fun id(): String = "UnusedScreenName"

    override fun description(): String = "Unused screen names should be implemented or removed."

    override fun explanation(): String = "All screen names should be implemented by @ScreenName annotation or removed from tracking plan."

    override fun detector(): Class<out Detector> = UnusedScreenNamesDetector::class.java
}
