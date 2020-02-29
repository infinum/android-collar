package co.infinum.collar.sample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.trackScreen
import co.infinum.collar.ui.CollarActivity
import kotlinx.android.synthetic.main.fragment_child_kotlin.*
import java.util.UUID

@ScreenName(value = KotlinScreenNames.CHILD_SCREEN)
class KotlinChildFragment : Fragment(R.layout.fragment_child_kotlin) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonProduceEvent4.setOnClickListener {
            trackEvent(AnalyticsEvent.Event4(UUID = UUID.randomUUID().toString()))
        }
        buttonStartUi.setOnClickListener {
            CollarActivity.start(it.context)
        }
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }
}