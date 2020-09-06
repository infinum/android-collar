package co.infinum.collar.lint.detectors.shared

import co.infinum.collar.lint.issues.shared.AbstractIssue
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UClass

@Suppress("UnstableApiUsage")
abstract class AbstractDetector : Detector(), SourceCodeScanner {

    abstract fun annotation(): String?

    abstract fun issue(): AbstractIssue

    abstract fun quickFix(): LintFix?

    internal fun report(context: JavaContext, declaration: UClass) =
        context.report(
            issue = issue()(),
            scopeClass = declaration,
            location = context.getNameLocation(declaration),
            message = issue().explanation(),
            quickfixData = quickFix()
        )
}