package com.infinum.collar.ui.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.infinum.collar.ui.R
import com.infinum.collar.ui.data.models.local.CollarEntity
import com.infinum.collar.ui.data.models.local.EntityType
import com.infinum.collar.ui.data.models.local.SettingsEntity
import com.infinum.collar.ui.databinding.CollarActivityCollarBinding
import com.infinum.collar.ui.extensions.addBadge
import com.infinum.collar.ui.extensions.presentationFormat
import com.infinum.collar.ui.extensions.safeDismiss
import com.infinum.collar.ui.extensions.searchView
import com.infinum.collar.ui.extensions.setup
import com.infinum.collar.ui.presentation.decorations.Decoration
import com.infinum.collar.ui.presentation.decorations.DotDecoration
import com.infinum.collar.ui.presentation.shared.base.BaseActivity
import com.infinum.collar.ui.presentation.shared.delegates.viewBinding
import com.infinum.collar.ui.presentation.shared.edgefactories.bounce.BounceEdgeEffectFactory
import java.util.Date
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class CollarActivity : BaseActivity<CollarState, CollarEvent>(), Toolbar.OnMenuItemClickListener {

    private lateinit var dialogFactory: CollarDialogFactory
    private var detailDialog: AlertDialog? = null

    private val entryAdapter: CollarAdapter = CollarAdapter(
        onListChanged = this@CollarActivity::showEmptyView,
        onClick = this@CollarActivity::showDetail
    )

    override val viewModel: CollarViewModel by viewModel()

    override val binding by viewBinding(CollarActivityCollarBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialogFactory = CollarDialogFactory(this)

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

        viewModel.entities()
        viewModel.settings()
    }

    override fun onDestroy() {
        detailDialog?.safeDismiss()
        super.onDestroy()
    }

    override fun onState(state: CollarState) {
        when (state) {
            is CollarState.Data -> entryAdapter.submitList(state.entities)
        }
    }

    override fun onEvent(event: CollarEvent) {
        when (event) {
            is CollarEvent.Settings -> onSettingsChanged(event.entity)
            is CollarEvent.Clear -> entryAdapter.submitList(null)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        with(binding) {
            when (item.itemId) {
                R.id.search -> onSearchStarted()
                R.id.clear -> viewModel.clearEntities()
                R.id.screens -> onFilterToggled(EntityType.SCREEN, item)
                R.id.events -> onFilterToggled(EntityType.EVENT, item)
                R.id.properties -> onFilterToggled(EntityType.PROPERTY, item)
                R.id.systemNotifications -> viewModel.notifications(
                    item.isChecked.not(),
                    toolbar.menu.findItem(R.id.inAppNotifications).isChecked
                )
                R.id.inAppNotifications -> viewModel.notifications(
                    toolbar.menu.findItem(R.id.systemNotifications).isChecked,
                    item.isChecked.not()
                )
            }
        }
        return true
    }

    private fun onSearchStarted() =
        with(binding.toolbar.menu) {
            findItem(R.id.filter).isVisible = false
            findItem(R.id.settings).isVisible = false
        }

    private fun onFilterToggled(type: EntityType, menuItem: MenuItem) {
        menuItem.isChecked = !menuItem.isChecked
        viewModel.filter(type, menuItem.isChecked)
    }

    private fun showDetail(entity: CollarEntity) {
        detailDialog = dialogFactory.entityDetail(entity)
        detailDialog?.show()
    }

    private fun showEmptyView(shouldShow: Boolean) {
        with(binding) {
            val isInSearch = toolbar.menu.findItem(R.id.settings).isVisible.not()
            emptyLayout.root.isVisible = shouldShow
            emptyLayout.instructionsButton.isVisible = isInSearch.not()
        }
    }

    private fun onSettingsChanged(settings: SettingsEntity) =
        with(binding) {
            toolbar.menu.findItem(R.id.systemNotifications).isChecked = settings.showSystemNotifications
            toolbar.menu.findItem(R.id.inAppNotifications).isChecked = settings.showInAppNotifications
            collectionStatusCard.isGone = settings.analyticsCollectionEnabled
            collectionStatusTimestamp.text =
                Date((settings.analyticsCollectionTimestamp)).presentationFormat
        }
}
