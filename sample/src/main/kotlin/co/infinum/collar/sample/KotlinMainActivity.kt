package co.infinum.collar.sample

import android.app.Activity
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import kotlinx.android.synthetic.main.activity_login.*

@ScreenName(value = "KotlinMainScreen")
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        trackEvent(AnalyticsEvent.OnCreate("KotlinMain"))

        button.setOnClickListener {
            doFoo()
            trackEvent(AnalyticsEvent.DoFooKotlin())
        }
    }

    private fun doFoo() {
        FooKotlin().trackFoo()
    }

    private fun onItemSelected(position: Int) {
        trackEvent(AnalyticsEvent.OnItemSelected(position, false))
    }
}
