package co.infinum.collar.sample

import android.app.Activity
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.trackScreen
import co.infinum.collar.ui.CollarActivity
import kotlinx.android.synthetic.main.activity_main_kotlin.*

@ScreenName(value = KotlinScreenNames.MAIN_SCREEN)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_kotlin)

        trackEvent(AnalyticsEvent.OnCreate("KotlinMain"))

        button.setOnClickListener {
            doFoo()
            trackEvent(AnalyticsEvent.DoFooKotlin())
        }

        trackProperty(UserProperty.UserType1(value = "retail"))

        buttonUi.setOnClickListener {
            CollarActivity.start(this)
        }
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    private fun doFoo() {
        FooKotlin().trackFoo()
    }

    private fun onItemSelected(position: Int) {
        trackEvent(AnalyticsEvent.OnItemSelected(position, false))
    }
}
