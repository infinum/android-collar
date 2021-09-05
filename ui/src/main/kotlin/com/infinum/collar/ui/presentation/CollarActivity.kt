package com.infinum.collar.ui.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.databinding.CollarActivityCollarBinding
import com.infinum.collar.ui.extensions.addBadge
import com.infinum.collar.ui.extensions.presentationFormat
import com.infinum.collar.ui.extensions.searchView
import com.infinum.collar.ui.extensions.setup
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_FILTER_EVENTS
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_FILTER_PROPERTIES
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_FILTER_SCREENS
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_REQUEST_CLEAR
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_REQUEST_FILTERS_APPLY
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_REQUEST_SETTINGS_APPLY
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_SETTINGS_IN_APP_NOTIFICATIONS
import com.infinum.collar.ui.presentation.Presentation.Constants.KEY_SETTINGS_SYSTEM_NOTIFICATIONS
import com.infinum.collar.ui.presentation.decorations.Decoration
import com.infinum.collar.ui.presentation.decorations.DotDecoration
import com.infinum.collar.ui.presentation.dialogs.CollarDetailDialog
import com.infinum.collar.ui.presentation.dialogs.CollarFilterDialog
import com.infinum.collar.ui.presentation.dialogs.CollarSettingsDialog
import com.infinum.collar.ui.presentation.shared.base.BaseActivity
import com.infinum.collar.ui.presentation.shared.delegates.viewBinding
import com.infinum.collar.ui.presentation.shared.edgefactories.bounce.BounceEdgeEffectFactory
import java.util.Date
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class CollarActivity : BaseActivity<CollarState, CollarEvent>(), Toolbar.OnMenuItemClickListener {

    private val entryAdapter: CollarAdapter = CollarAdapter(
        onListChanged = this@CollarActivity::showEmptyView,
        onClick = this@CollarActivity::showDetail
    )

    override val viewModel: CollarViewModel by viewModel()

    override val binding by viewBinding(CollarActivityCollarBinding::inflate)

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            with(toolbar) {
                navigationIcon = applicationInfo.loadIcon(packageManager)?.addBadge(this@CollarActivity)
                subtitle = applicationInfo.loadLabel(packageManager)
                setNavigationOnClickListener { finish() }
                setOnMenuItemClickListener(this@CollarActivity)
                menu.searchView?.setup(
                    componentName = componentName,
                    onSearchClosed = {
                        with(menu) {
                            findItem(R.id.filter).isVisible = true
                            findItem(R.id.settings).isVisible = true
                        }
                    },
                    onQueryTextChanged = {
                        viewModel.search(it)
                    }
                )
            }

            with(recyclerView) {
                addItemDecoration(DotDecoration(context, Decoration.START))
                addItemDecoration(DotDecoration(context, Decoration.END))
                layoutManager = LinearLayoutManager(
                    context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = entryAdapter
                edgeEffectFactory = BounceEdgeEffectFactory()
            }
            emptyLayout.instructionsButton.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .apply {
                            data = Uri.parse(getString(R.string.collar_check_setup_link))
                        }
                )
            }
        }

        with(supportFragmentManager) {
            setFragmentResultListener(KEY_REQUEST_FILTERS_APPLY, this@CollarActivity) { _, bundle ->
                viewModel.filter(
                    bundle.getBoolean(KEY_FILTER_SCREENS, true),
                    bundle.getBoolean(KEY_FILTER_EVENTS, true),
                    bundle.getBoolean(KEY_FILTER_PROPERTIES, true)
                )
            }
            setFragmentResultListener(KEY_REQUEST_SETTINGS_APPLY, this@CollarActivity) { _, bundle ->
                viewModel.notifications(
                    bundle.getBoolean(KEY_SETTINGS_SYSTEM_NOTIFICATIONS, true),
                    bundle.getBoolean(KEY_SETTINGS_IN_APP_NOTIFICATIONS, true)
                )
            }
            setFragmentResultListener(KEY_REQUEST_CLEAR, this@CollarActivity) { _, _ ->
                viewModel.clearEntities()
            }
        }

        viewModel.entities()
        viewModel.settings()
    }

    override fun onState(state: CollarState) {
        when (state) {
            is CollarState.Data -> entryAdapter.submitList(state.entities)
        }
    }

    override fun onEvent(event: CollarEvent) {
        when (event) {
            is CollarEvent.Filters -> CollarFilterDialog.newInstance(
                screens = event.screens,
                events = event.events,
                properties = event.properties
            ).show(supportFragmentManager, null)
            is CollarEvent.Settings -> CollarSettingsDialog.newInstance(
                systemNotifications = event.showSystemNotifications,
                inAppNotifications = event.showInAppNotifications
            ).show(supportFragmentManager, null)
            is CollarEvent.SettingsChanged -> onSettingsChanged(
                event.analyticsCollectionEnabled,
                event.analyticsCollectionTimestamp
            )
            is CollarEvent.Clear -> entryAdapter.submitList(null)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> onSearchStarted()
            R.id.filter -> viewModel.filters()
            R.id.settings -> viewModel.showSettings()
        }
        return true
    }

    private fun onSearchStarted() =
        with(binding.toolbar.menu) {
            findItem(R.id.filter).isVisible = false
            findItem(R.id.settings).isVisible = false
        }

    private fun showDetail(entity: CollarEntity) {
        CollarDetailDialog.newInstance(entity).show(supportFragmentManager, null)
    }

    private fun showEmptyView(shouldShow: Boolean) {
        with(binding) {
            val isInSearch = toolbar.menu.findItem(R.id.settings).isVisible.not()
            emptyLayout.root.isVisible = shouldShow
            emptyLayout.instructionsButton.isVisible = isInSearch.not()
        }
    }

    private fun onSettingsChanged(analyticsCollectionEnabled: Boolean, analyticsCollectionTimestamp: Long) =
        with(binding) {
            collectionStatusCard.isGone = analyticsCollectionEnabled
            collectionStatusTimestamp.text = Date((analyticsCollectionTimestamp)).presentationFormat
        }
}
