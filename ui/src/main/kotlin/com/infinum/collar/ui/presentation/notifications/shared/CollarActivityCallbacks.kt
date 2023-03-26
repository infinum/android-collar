package com.infinum.collar.ui.presentation.notifications.shared

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal interface CollarActivityCallbacks : Application.ActivityLifecycleCallbacks {

    fun current(): Activity?

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
}
