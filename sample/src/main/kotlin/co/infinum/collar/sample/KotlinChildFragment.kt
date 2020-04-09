package co.infinum.collar.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.analytics.AnalyticsEvent
import co.infinum.collar.sample.analytics.AnalyticsScreens
import co.infinum.collar.sample.analytics.trackEvent
import co.infinum.collar.ui.CollarUi
import kotlinx.android.synthetic.main.fragment_child_kotlin.*

@ScreenName(value = AnalyticsScreens.CHILD_SCREEN)
class KotlinChildFragment : Fragment(R.layout.fragment_child_kotlin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonProduceEvent4.setOnClickListener {
            trackEvent(AnalyticsEvent.CheckTypes(true, "check", 0, 0.0))
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