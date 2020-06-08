package co.infinum.collar.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.infinum.collar.Collar.Companion.trackEvent
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.analytics.TrackingPlanEvents
import co.infinum.collar.sample.analytics.TrackingPlanScreens
import co.infinum.collar.sample.analytics.trackEvent
import co.infinum.collar.ui.CollarUi
import kotlinx.android.synthetic.main.fragment_child_kotlin.*

@ScreenName(value = TrackingPlanScreens.HOME)
class KotlinChildFragment : Fragment(R.layout.fragment_child_kotlin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonProduceEvent4.setOnClickListener {
            trackEvent(TrackingPlanEvents.AlexaConnect(
                TrackingPlanEvents.AlexaConnect.StatusEnum.SUCCESS.toString(),
                TrackingPlanEvents.AlexaConnect.SourceEnum.ONBOARDING.toString()
            ))
        }
        buttonStartUi.setOnClickListener {
            startActivity(
                CollarUi.launchIntent(it.context)
            )
        }
    }

    override fun onResume() {
        super.onResume()

    }
}