package co.infinum.collar.sample

import androidx.fragment.app.Fragment
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.trackScreen

@ScreenName("KotlinAndroidXFragment")
class KotlinAndroidXFragment : Fragment() {

    // whatever

    override fun onResume() {
        super.onResume()

        trackScreen()
    }
}