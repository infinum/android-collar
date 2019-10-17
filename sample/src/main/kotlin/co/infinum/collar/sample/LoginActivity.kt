package co.infinum.collar.sample

import android.app.Activity
import android.os.Bundle
import co.infinum.collar.Trackable
import co.infinum.collar.annotations.Attribute
import co.infinum.collar.annotations.ConstantAttribute
import co.infinum.collar.annotations.ConvertAttribute
import co.infinum.collar.annotations.ConvertAttributes
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.annotations.TrackEvent
import kotlinx.android.synthetic.main.activity_login.*

@ScreenName(value = "LoginScreen")
class LoginActivity : Activity(), Trackable {

    @TrackEvent("onCreate")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        button.setOnClickListener {
            doFoo()
        }
    }

    override fun trackableAttributes(): MutableMap<String, Any> =
        mutableMapOf(
            "username" to "stallion69@yahoo.co.uk",
            "accepted" to true
        )

    @TrackEvent("buttonClickKotlin")
    @ConstantAttribute(key = "buttonNameKotlin", value = "trackFooKotlin")
    private fun doFoo() {
        FooKotlin().trackFoo()
    }

    @Suppress("UNUSED_PARAMETER")
    @TrackEvent("onItemSelectedKotlin")
    @ConvertAttributes(keys = [1, 2], values = ["finishedKotlin", "acceptedKotlin"])
    private fun onItemSelected(@ConvertAttribute("statusKotlin") position: Int) {
        // do something cool with selected item
    }

    @TrackEvent("someEventKotlin")
    @Attribute("userIdKotlin") // This attribute will use return value as attribute value.
    private fun userId(): String {
        return "471104"
    }
}