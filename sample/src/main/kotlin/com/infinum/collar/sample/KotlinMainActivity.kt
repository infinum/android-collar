package com.infinum.collar.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.infinum.collar.annotations.ScreenName
import com.infinum.collar.sample.databinding.ActivityMainKotlinBinding
import com.infinum.collar.trackScreen

@ScreenName(enabled = true)
class KotlinMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val viewBinding = ActivityMainKotlinBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        viewBinding.buttonProduceEvent3.setOnClickListener {
            trackEvent(
                AnalyticsEvent.EventThree(
                    myString = "cool",
                    myBoolean = false,
                    myMap = mapOf(
                        "1" to 1,
                        "2" to 2
                    )
                )
            )
        }

        viewBinding.buttonShowKotlinChild.setOnClickListener {
            showKotlinChild()
        }

        trackProperty(UserProperty.LanguageType(value = "Kotlin", mapOf("shouldSendEventToProviderB" to false)))
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    private fun showKotlinChild() = startActivity(Intent(this, KotlinChildActivity::class.java))
}
