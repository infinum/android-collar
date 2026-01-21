package com.infinum.collar.lint.shared

import com.android.tools.lint.checks.infrastructure.LintDetectorTest.java
import com.android.tools.lint.checks.infrastructure.TestFile

@Suppress("UnstableApiUsage")
internal object Stubs {

    val ACTIVITY: TestFile = java(
        """
                package android.app;

                public class Activity {
                    
                }
            """
    ).indented()

    val APPCOMPAT_ACTIVITY: TestFile = java(
        """
                package androidx.appcompat.app;

                import android.app.Activity;

                public class AppCompatActivity extends Activity {
                    
                }
            """
    ).indented()
}
