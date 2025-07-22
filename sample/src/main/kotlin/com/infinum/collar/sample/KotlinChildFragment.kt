package com.infinum.collar.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.infinum.collar.annotations.ScreenName
import com.infinum.collar.sample.databinding.FragmentChildKotlinBinding
import com.infinum.collar.trackScreen
import com.infinum.collar.ui.CollarUi
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
            CollarUi.show()
        }
    }

    override fun onResume() {
        super.onResume()

        trackScreen(mapOf(Pair("shouldSendEventToProviderA", true)))
    }

    override fun onDestroy() {
        super.onDestroy()

        viewBinding = null
    }
}
