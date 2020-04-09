package co.infinum.collar.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.analytics.*
import co.infinum.collar.trackScreen
import kotlinx.android.synthetic.main.activity_main_kotlin.*
import java.util.*

@ScreenName(value = AnalyticsScreens.MAIN_SCREEN)
class KotlinMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_kotlin)

        buttonProduceEvent3.setOnClickListener {
            trackEvent(AnalyticsEvent.LoginUser(AnalyticsEvent.LoginUser.LanguageTypeEnum.KOTLIN.toString()))
        }

        buttonShowKotlinChild.setOnClickListener {
            showKotlinChild()
        }

        trackProperty( UserProperty.UserId(UUID.randomUUID().toString()));
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    private fun showKotlinChild() = startActivity(Intent(this, KotlinChildActivity::class.java))
}
