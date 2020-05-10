package co.infinum.collar.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.infinum.collar.annotations.ScreenName
import co.infinum.collar.sample.databinding.FragmentChildKotlinBinding
import co.infinum.collar.trackScreen
import co.infinum.collar.ui.CollarUi
import java.util.UUID

@ScreenName(value = KotlinScreenNames.CHILD_SCREEN, enabled = true)
class KotlinChildFragment : Fragment() {

    private var viewBinding: FragmentChildKotlinBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewBinding = FragmentChildKotlinBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.buttonProduceEvent4?.setOnClickListener {
            trackEvent(AnalyticsEvent.Event4(uuid = UUID.randomUUID().toString(), userType = "CORPORATE"))
        }
        viewBinding?.buttonStartUi?.setOnClickListener {
            startActivity(
                CollarUi.launchIntent()
            )
        }
    }

    override fun onResume() {
        super.onResume()

        trackScreen()
    }

    override fun onDestroy() {
        super.onDestroy()

        viewBinding = null
    }
}
