package co.infinum.collar.sample

import android.app.Activity
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import kotlinx.android.synthetic.main.activity_login.*
import trackScreen

@ScreenName(value = "KotlinMainActivity")
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
