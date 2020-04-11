package co.infinum.collar.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.trackScreen
import kotlinx.android.synthetic.main.activity_main_kotlin.*

@ScreenName(value = KotlinScreenNames.MAIN_SCREEN, enabled = true)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_kotlin)

        buttonProduceEvent3.setOnClickListener {
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
                    "3" to 3
                }
            ))
        }

        buttonShowKotlinChild.setOnClickListener {
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
