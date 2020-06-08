package co.infinum.collar.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.infinum.collar.Collar.Companion.trackEvent
import co.infinum.collar.Collar.Companion.trackProperty
import co.infinum.collar.Collar.Companion.trackScreen
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.analytics.*
import co.infinum.collar.trackScreen
import kotlinx.android.synthetic.main.activity_main_kotlin.*
import java.util.*

@ScreenName(value = TrackingPlanScreens.HOME)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_kotlin)

        buttonProduceEvent3.setOnClickListener {
            trackEvent(TrackingPlanEvents.MachineTurnOff())
        }

        buttonShowKotlinChild.setOnClickListener {
            showKotlinChild()
        }

        trackProperty( TrackingPlanUserProperties.MachineModel("some model"));
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    private fun showKotlinChild() = startActivity(Intent(this, KotlinChildActivity::class.java))
}
