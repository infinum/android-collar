package com.infinum.collar.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.infinum.collar.annotations.ScreenName
// import com.infinum.collar.sample.analytics.trackingplan.TrackingPlanEvents
// import com.infinum.collar.sample.analytics.trackingplan.TrackingPlanScreens
// import com.infinum.collar.sample.analytics.trackingplan.trackEvent
import com.infinum.collar.sample.databinding.ActivityMainKotlinBinding
import com.infinum.collar.trackScreen

@ScreenName(enabled = true)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityMainKotlinBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

//        trackEvent(
//            TrackingPlanEvents.AlexaConnect("", "")
//        )

        viewBinding.buttonProduceEvent3.setOnClickListener {
            trackEvent(
                AnalyticsEvent.EventThree(
                    myString = "cool",
                    myBoolean = false
                )
            )
        }

        viewBinding.buttonShowKotlinChild.setOnClickListener {
            showKotlinChild()
        }

        trackProperty(UserProperty.LanguageType(value = "Kotlin"))
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    private fun showKotlinChild() = startActivity(Intent(this, KotlinChildActivity::class.java))
}
