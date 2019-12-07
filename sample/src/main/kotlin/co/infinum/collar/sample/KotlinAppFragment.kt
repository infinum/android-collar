@file:Suppress("DEPRECATION")

package co.infinum.collar.sample

import android.app.Fragment
import co.infinum.collar.annotations.ScreenName
import trackScreen

@ScreenName("KotlinAppFragment")
class KotlinAppFragment : Fragment() {

    // whatever

    override fun onResume() {
        super.onResume()

        trackScreen()
    }
}