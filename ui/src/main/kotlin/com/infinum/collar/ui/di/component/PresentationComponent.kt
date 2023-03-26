package com.infinum.collar.ui.di.component

import android.content.Context
import android.content.Intent
import com.infinum.collar.ui.di.scope.PresentationScope
import com.infinum.collar.ui.domain.Repositories
import com.infinum.collar.ui.presentation.CollarActivity
import com.infinum.collar.ui.presentation.notifications.inapp.InAppNotificationFactory
import com.infinum.collar.ui.presentation.notifications.shared.CollarActivityCallbacks
import com.infinum.collar.ui.presentation.notifications.shared.CollarActivityLifecycleCallbacks
import com.infinum.collar.ui.presentation.notifications.system.SystemNotificationFactory
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@PresentationScope
internal abstract class PresentationComponent(
    @get:Provides val context: Context,
    @Component val viewModelComponent: ViewModelComponent
) {

    fun launchIntent(): Intent =
        Intent(context, CollarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

    fun show() = context.startActivity(launchIntent())

    @Provides
    @PresentationScope
    fun callbacks(): CollarActivityCallbacks =
        CollarActivityLifecycleCallbacks()

    abstract val systemNotificationFactory: SystemNotificationFactory

    abstract val inAppNotificationFactory: InAppNotificationFactory

    abstract val entities: Repositories.Entity

    abstract val settings: Repositories.Settings
}
