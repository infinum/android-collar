package com.infinum.collar.ui.presentation.dialogs

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.infinum.collar.ui.R
import com.infinum.collar.ui.databinding.CollarDialogFilterBinding
import com.infinum.collar.ui.presentation.shared.Constants.KEY_FILTER_EVENTS
import com.infinum.collar.ui.presentation.shared.Constants.KEY_FILTER_PROPERTIES
import com.infinum.collar.ui.presentation.shared.Constants.KEY_FILTER_SCREENS
import com.infinum.collar.ui.presentation.shared.Constants.KEY_REQUEST_FILTERS_APPLY
import com.infinum.collar.ui.presentation.shared.base.BaseBottomSheetDialogFragment
import com.infinum.collar.ui.presentation.shared.base.BaseViewModel
import com.infinum.collar.ui.presentation.shared.delegates.viewBinding

internal class CollarFilterDialog : BaseBottomSheetDialogFragment<Any, Any>(R.layout.collar_dialog_filter) {

    companion object {
        fun newInstance(screens: Boolean, events: Boolean, properties: Boolean): CollarFilterDialog {
            val fragment = CollarFilterDialog()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_FILTER_SCREENS, screens)
                putBoolean(KEY_FILTER_EVENTS, events)
                putBoolean(KEY_FILTER_PROPERTIES, properties)
            }
            return fragment
        }
    }

    override val binding: CollarDialogFilterBinding by viewBinding(
        CollarDialogFilterBinding::bind
    )

    override val viewModel: BaseViewModel<Any, Any>? = null

    private var screens: Boolean = true
    private var events: Boolean = true
    private var properties: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            screens = it.getBoolean(KEY_FILTER_SCREENS)
            events = it.getBoolean(KEY_FILTER_EVENTS)
            properties = it.getBoolean(KEY_FILTER_PROPERTIES)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindToolbar()

        with(binding) {
            screensButton.isChecked = screens
            eventsButton.isChecked = events
            propertiesButton.isChecked = properties

            screensButton.addOnCheckedChangeListener { _, isChecked ->
                screens = isChecked
            }
            eventsButton.addOnCheckedChangeListener { _, isChecked ->
                events = isChecked
            }
            propertiesButton.addOnCheckedChangeListener { _, isChecked ->
                properties = isChecked
            }
        }
    }

    override fun onState(state: Any) = Unit

    override fun onEvent(event: Any) = Unit

    private fun bindToolbar() =
        with(binding) {
            toolbar.setNavigationOnClickListener { dismiss() }
            toolbar.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.apply -> {
                        applyResult()
                        dismiss()
                        true
                    }
                    else -> false
                }
            }
        }

    private fun applyResult() =
        setFragmentResult(
            KEY_REQUEST_FILTERS_APPLY,
            bundleOf(
                KEY_FILTER_SCREENS to screens,
                KEY_FILTER_EVENTS to events,
                KEY_FILTER_PROPERTIES to properties
            )
        )
}
