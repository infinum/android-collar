package com.infinum.collar.lint.shared

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.TestFile

@Suppress("UnstableApiUsage")
internal object Stubs {

    val APPCOMPAT_ACTIVITY: TestFile = java(
        """
                package androidx.appcompat.app;

                public class AppCompatActivity {
                    
                }
            """
    ).indented()
}
