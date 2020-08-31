package co.infinum.collar.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.analytics.trackingplan.TrackingPlanScreens
import co.infinum.collar.sample.databinding.ActivityMainKotlinBinding
import co.infinum.collar.trackScreen

@ScreenName(value = TrackingPlanScreens.HOME, enabled = true)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityMainKotlinBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        viewBinding.buttonProduceEvent3.setOnClickListener {
            trackEvent(AnalyticsEvent.EventThree(
                myString = "cool",
                myBoolean = false,
                myByte = 101,
                myChar = Character.MIN_VALUE,
                myDouble = 24.7,
                myFloat = 11.5f,
                myInt = 1,
                myLong = 198L,
                myShort = 4096,
                myBundle = Bundle().apply {
                    "1" to 1
                    "2" to 2
                    "3" to 1
                }
            ))
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
