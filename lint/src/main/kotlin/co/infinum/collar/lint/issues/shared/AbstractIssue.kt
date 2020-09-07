package co.infinum.collar.lint.issues.shared

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import java.util.EnumSet

@Suppress("UnstableApiUsage")
abstract class AbstractIssue : BaseIssue {

    abstract fun id(): String

    abstract fun description(): String

    abstract fun explanation(): String

    abstract fun detector(): Class<out Detector>

    private fun category(): Category = Category.CORRECTNESS

    private fun priority(): Int = 5

    private fun severity(): Severity = Severity.WARNING

    private fun scope(): EnumSet<Scope> = Scope.JAVA_FILE_SCOPE

    override operator fun invoke(): Issue = Issue.create(
        id = id(),
        briefDescription = description(),
        explanation = explanation(),
        category = category(),
        priority = priority(),
        severity = severity(),
        implementation = Implementation(
            detector(),
            scope()
        )
    )
}