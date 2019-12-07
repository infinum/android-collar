package co.infinum.collar.sample

import androidx.appcompat.app.AppCompatActivity
import co.infinum.collar.annotations.ScreenName
import trackScreen

@ScreenName("KotlinAndroidXActivity")
class KotlinAndroidXActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        trackScreen()
    }
}