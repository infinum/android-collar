package com.infinum.collar.ui.presentation.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.infinum.collar.ui.R
import com.infinum.collar.ui.databinding.CollarDialogSettingsBinding
import com.infinum.collar.ui.presentation.shared.Constants.KEY_REQUEST_CLEAR
import com.infinum.collar.ui.presentation.shared.Constants.KEY_REQUEST_SETTINGS_APPLY
import com.infinum.collar.ui.presentation.shared.Constants.KEY_SETTINGS_IN_APP_NOTIFICATIONS
import com.infinum.collar.ui.presentation.shared.Constants.KEY_SETTINGS_SYSTEM_NOTIFICATIONS
import com.infinum.collar.ui.presentation.shared.base.BaseBottomSheetDialogFragment
import com.infinum.collar.ui.presentation.shared.base.BaseViewModel
import com.infinum.collar.ui.presentation.shared.delegates.viewBinding

internal class CollarSettingsDialog : BaseBottomSheetDialogFragment<Any, Any>(R.layout.collar_dialog_settings) {

    companion object {
        fun newInstance(systemNotifications: Boolean, inAppNotifications: Boolean): CollarSettingsDialog {
            val fragment = CollarSettingsDialog()
            fragment.arguments = Bundle().apply {
                putBoolean(KEY_SETTINGS_SYSTEM_NOTIFICATIONS, systemNotifications)
                putBoolean(KEY_SETTINGS_IN_APP_NOTIFICATIONS, inAppNotifications)
            }
            return fragment
        }
    }

    override val binding: CollarDialogSettingsBinding by viewBinding(
        CollarDialogSettingsBinding::bind
    )

    override val viewModel: BaseViewModel<Any, Any>? = null

    private var systemNotifications: Boolean = true
    private var inAppNotifications: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            systemNotifications = it.getBoolean(KEY_SETTINGS_SYSTEM_NOTIFICATIONS)
            inAppNotifications = it.getBoolean(KEY_SETTINGS_IN_APP_NOTIFICATIONS)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindToolbar()

        with(binding) {
            systemNotificationsButton.isChecked = systemNotifications
            inAppNotificationsButton.isChecked = inAppNotifications

            systemNotificationsButton.addOnCheckedChangeListener { _, isChecked ->
                systemNotifications = isChecked
            }
            inAppNotificationsButton.addOnCheckedChangeListener { _, isChecked ->
                inAppNotifications = isChecked
            }
            clearButton.setOnClickListener {
                setFragmentResult(
                    KEY_REQUEST_CLEAR,
                    bundleOf()
                )
                dismiss()
            }
        }
    }

    override fun onState(state: Any) = Unit

    override fun onEvent(event: Any) = Unit

    private fun applyResult() =
        setFragmentResult(
            KEY_REQUEST_SETTINGS_APPLY,
            bundleOf(
                KEY_SETTINGS_SYSTEM_NOTIFICATIONS to systemNotifications,
                KEY_SETTINGS_IN_APP_NOTIFICATIONS to inAppNotifications
            )
        )

    private fun bindToolbar() =
        with(binding) {
            toolbar.setNavigationOnClickListener { dismiss() }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.apply -> {
                        applyResult()
                        dismiss()
                        true
                    }
                    else -> false
                }
            }
        }
}
