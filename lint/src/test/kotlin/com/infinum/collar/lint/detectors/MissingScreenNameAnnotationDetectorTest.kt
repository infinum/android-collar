package com.infinum.collar.lint.detectors

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue
import com.infinum.collar.lint.IssueFactory
import com.infinum.collar.lint.shared.Stubs
import org.junit.jupiter.api.Test

@Suppress("UnstableApiUsage")
internal class MissingScreenNameAnnotationDetectorTest : LintDetectorTest() {

    private val missingAnnotationKotlin = kotlin(
        """
        import androidx.appcompat.app.AppCompatActivity
        
        class KotlinChildActivity : AppCompatActivity() {
       
        }
        """.trimIndent()
    )

    override fun getDetector(): Detector =
        MissingScreenNameAnnotationDetector()

    override fun getIssues(): MutableList<Issue> =
        mutableListOf(
            IssueFactory.MISSING_SCREEN_NAME_ANNOTATION
        )

    @Test
    fun incorrectMethodUsage_shouldReportAWarning() {
        lint()
            .files(Stubs.APPCOMPAT_ACTIVITY, missingAnnotationKotlin)
            .run()
            .expectWarningCount(1)
    }
}
