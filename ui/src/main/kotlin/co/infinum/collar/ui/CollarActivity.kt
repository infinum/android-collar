package co.infinum.collar.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ShareCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.databinding.CollarActivityCollarBinding
import co.infinum.collar.ui.databinding.CollarViewDetailBinding
import co.infinum.collar.ui.decorations.LastDotDecoration
import co.infinum.collar.ui.extensions.addBadge
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class CollarActivity : AppCompatActivity() {

    companion object {
        private const val FORMAT_DATETIME = "dd.MM.yyyy. HH:mm:ss"
    }

    private lateinit var viewBinding: CollarActivityCollarBinding

    private lateinit var viewModel: CollarViewModel

    private val entryAdapter: CollarAdapter = CollarAdapter(
        onClick = this@CollarActivity::showDetail
    )

    override fun onCreate(savedInstanceState: Bundle?) =
        super.onCreate(savedInstanceState).run {
            viewBinding = CollarActivityCollarBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)

            setupToolbar()
            setupRecyclerView()
            setupEmptyView()
            setupViewModel()
        }

    private fun setupToolbar() {
        with(viewBinding.toolbar) {
            navigationIcon = applicationInfo.loadIcon(packageManager)?.addBadge(this@CollarActivity)
            subtitle = applicationInfo.loadLabel(packageManager)
            setNavigationOnClickListener { finish() }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clear -> clear()
                    R.id.screens -> {
                        it.isChecked = !it.isChecked
                        viewModel.filter(EntityType.SCREEN, it.isChecked)
                    }
                    R.id.events -> {
                        it.isChecked = !it.isChecked
                        viewModel.filter(EntityType.EVENT, it.isChecked)
                    }
                    R.id.properties -> {
                        it.isChecked = !it.isChecked
                        viewModel.filter(EntityType.PROPERTY, it.isChecked)
                    }
                    R.id.systemNotifications -> {
                        viewModel.notifications(it.isChecked.not(), menu.findItem(R.id.inAppNotifications).isChecked)
                    }
                    R.id.inAppNotifications -> {
                        viewModel.notifications(menu.findItem(R.id.systemNotifications).isChecked, it.isChecked.not())
                    }
                }
                true
            }
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            (menu.findItem(R.id.search).actionView as SearchView).apply {
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
                setIconifiedByDefault(true)
                isSubmitButtonEnabled = false
                isQueryRefinementEnabled = true
                maxWidth = Integer.MAX_VALUE
                setOnQueryTextFocusChangeListener { _, hasFocus ->
                    menu.findItem(R.id.filter).isVisible = hasFocus.not()
                    menu.findItem(R.id.settings).isVisible = hasFocus.not()
                }
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        search(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        search(newText)
                        return true
                    }
                })
            }
        }
    }

    private fun setupRecyclerView() {
        with(viewBinding.recyclerView) {
            addItemDecoration(LastDotDecoration(context))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = entryAdapter
        }
    }

    private fun setupEmptyView() {
        viewBinding.emptyLayout.instructionsView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CollarViewModel::class.java)
        viewModel.initialize(this)
        if (viewModel.entities().hasObservers().not()) {
            viewModel.entities().observe(this) {
                entryAdapter.addItems(it)
                viewBinding.emptyLayout.root.isVisible = entryAdapter.itemCount == 0
            }
        }
        if (viewModel.settings().hasObservers().not()) {
            viewModel.settings().observe(this) {
                with(viewBinding.toolbar) {
                    menu.findItem(R.id.systemNotifications).isChecked = it.showSystemNotifications
                    menu.findItem(R.id.inAppNotifications).isChecked = it.showInAppNotifications
                }
            }
        }
    }

    override fun onDestroy() {
        if (viewModel.entities().hasObservers()) {
            viewModel.entities().removeObservers(this)
        }
        if (viewModel.settings().hasObservers()) {
            viewModel.settings().removeObservers(this)
        }
        super.onDestroy()
    }

    private fun clear() {
        entryAdapter.clear()
        viewModel.delete()
    }

    private fun search(query: String?) = viewModel.search(query)

    @SuppressLint("DefaultLocale")
    @Suppress("ComplexMethod")
    private fun showDetail(entity: CollarEntity) {
        MaterialAlertDialogBuilder(this)
            .setIcon(
                when (entity.type) {
                    EntityType.SCREEN -> R.drawable.collar_ic_screen_detail
                    EntityType.EVENT -> R.drawable.collar_ic_event_detail
                    EntityType.PROPERTY -> R.drawable.collar_ic_property_detail
                    else -> 0
                }
            )
            .setTitle(entity.type?.name?.toLowerCase()?.capitalize())
            .setView(
                CollarViewDetailBinding.inflate(layoutInflater)
                    .apply {
                        timeView.text = SimpleDateFormat(
                            FORMAT_DATETIME,
                            Locale.getDefault()
                        ).format(Date(entity.timestamp))
                        nameView.text = entity.name
                        valueCaptionView.text = when (entity.type) {
                            EntityType.EVENT -> entity.parameters?.let { getString(R.string.collar_parameters) }
                            EntityType.PROPERTY -> getString(R.string.collar_value)
                            else -> null
                        }.also {
                            if (it.isNullOrBlank()) {
                                valueView.isGone = true
                                valueCaptionView.isGone = true
                            } else {
                                valueView.isVisible = true
                                valueCaptionView.isVisible = true
                                valueView.text = when (entity.type) {
                                    EntityType.EVENT -> entity.parameters
                                    EntityType.PROPERTY -> entity.value
                                    else -> null
                                }
                            }
                        }
                    }.root
            )
            .setPositiveButton(R.string.collar_share) { _, _ -> share(entity) }
            .create()
            .show()
    }

    private fun share(entity: CollarEntity) {
        ShareCompat.IntentBuilder.from(this)
            .setChooserTitle(R.string.collar_name)
            .setType("text/plain")
            .setText(
                listOfNotNull(
                    "time: ${SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(entity.timestamp))}",
                    entity.name?.let { "name: $it" },
                    entity.type?.let { "type: ${it.name.toLowerCase(Locale.getDefault())}" },
                    entity.value?.let { "value: $it" },
                    entity.parameters?.let { "parameters: $it" }
                ).joinToString("\n")
            )
            .startChooser()
    }
}
