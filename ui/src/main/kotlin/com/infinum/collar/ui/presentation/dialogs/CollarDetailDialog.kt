package com.infinum.collar.ui.presentation.dialogs

import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.databinding.CollarDialogDetailBinding
import com.infinum.collar.ui.extensions.presentationFormat
import com.infinum.collar.ui.presentation.Presentation
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_ENTITY_NAME
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_ENTITY_PARAMETERS
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_ENTITY_TIMESTAMP
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_ENTITY_TYPE
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_ENTITY_VALUE
import com.infinum.collar.ui.presentation.shared.base.BaseBottomSheetDialogFragment
import com.infinum.collar.ui.presentation.shared.base.BaseViewModel
import com.infinum.collar.ui.presentation.shared.delegates.viewBinding
import java.util.Date
import java.util.Locale

internal class CollarDetailDialog : BaseBottomSheetDialogFragment<Any, Any>(R.layout.collar_dialog_detail) {

    companion object {
        fun newInstance(entity: CollarEntity): CollarDetailDialog {
            val fragment = CollarDetailDialog()
            fragment.arguments = Bundle().apply {
                putLong(KEY_ENTITY_TIMESTAMP, entity.timestamp)
                putString(KEY_ENTITY_NAME, entity.name)
                entity.type?.ordinal?.let { putInt(KEY_ENTITY_TYPE, it) }
                putString(KEY_ENTITY_VALUE, entity.value)
                putString(KEY_ENTITY_PARAMETERS, entity.parameters)
            }
            return fragment
        }
    }

    override val binding: CollarDialogDetailBinding by viewBinding(
        CollarDialogDetailBinding::bind
    )

    override val viewModel: BaseViewModel<Any, Any>? = null

    private var timestamp: Long? = null
    private var name: String? = null
    private var type: EntityType? = null
    private var value: String? = null
    private var parameters: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            timestamp = it.getLong(KEY_ENTITY_TIMESTAMP)
            name = it.getString(KEY_ENTITY_NAME)
            type = EntityType.values()[it.getInt(KEY_ENTITY_TYPE)]
            value = it.getString(KEY_ENTITY_VALUE)
            parameters = it.getString(KEY_ENTITY_PARAMETERS)
        }
    }

    @Suppress("ComplexMethod", "LongMethod")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            appBarLayout.isLiftOnScroll = true
            appBarLayout.isLifted = false
            toolbar.setNavigationOnClickListener {
                dismiss()
            }
            toolbar.title = type
                ?.name
                ?.lowercase(Locale.getDefault())
                ?.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.share -> {
                        share()
                        true
                    }
                    else -> false
                }
            }
            when (type) {
                EntityType.SCREEN -> R.drawable.collar_ic_screen
                EntityType.EVENT -> R.drawable.collar_ic_event
                EntityType.PROPERTY -> R.drawable.collar_ic_property
                else -> null
            }?.let {
                iconView.setImageResource(it)
            } ?: run { iconView.isGone = true }
            timeView.text = timestamp?.let { Date(it).presentationFormat }
            nameView.text = name
            valueCaptionView.text = when (type) {
                EntityType.EVENT -> parameters?.let {
                    getString(R.string.collar_parameters)
                }
                EntityType.PROPERTY -> getString(R.string.collar_value)
                else -> null
            }.also {
                if (it.isNullOrBlank()) {
                    valueView.isGone = true
                    valueCaptionView.isGone = true
                } else {
                    valueView.isVisible = true
                    valueCaptionView.isVisible = true
                    valueView.text = when (type) {
                        EntityType.EVENT -> parameters
                        EntityType.PROPERTY -> value
                        else -> null
                    }
                }
            }
            contentView.setOnScrollChangeListener(
                NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, oldScrollY ->
                    if (scrollY > oldScrollY) {
                        appBarLayout.isLifted = true
                    }
                    if (scrollY < oldScrollY) {
                        appBarLayout.isLifted = true
                    }
                    if (scrollY == 0) {
                        appBarLayout.isLifted = false
                    }
                    if (scrollY == v.measuredHeight - v.getChildAt(0).measuredHeight) {
                        appBarLayout.isLifted = true
                    }
                }
            )
        }
    }

    override fun onState(state: Any) = Unit

    override fun onEvent(event: Any) = Unit

    private fun share() {
        ShareCompat.IntentBuilder(requireContext())
            .setChooserTitle(R.string.collar_name)
            .setType(Presentation.Constants.MIME_TYPE_TEXT)
            .setText(
                listOfNotNull(
                    timestamp?.let { "time: ${Date(it).presentationFormat}" },
                    name?.let { "name: $it" },
                    type?.let { "type: ${it.name.lowercase(Locale.getDefault())}" },
                    value?.let { "value: $it" },
                    parameters?.let { "parameters: $it" }
                ).joinToString("\n")
            )
            .startChooser()
    }
}
