package com.infinum.collar.ui.presentation.notifications.shared

import android.app.Activity
import android.os.Bundle
import me.tatarka.inject.annotations.Inject

@Inject
internal class CollarActivityLifecycleCallbacks : CollarActivityCallbacks {

    private var currentActivity: Activity? = null

    override fun current(): Activity? = currentActivity

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
}
