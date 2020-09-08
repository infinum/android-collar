package co.infinum.collar.ui.presentation

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import co.infinum.collar.ui.R
import co.infinum.collar.ui.data.models.local.CollarEntity
import co.infinum.collar.ui.data.models.local.EntityType
import co.infinum.collar.ui.databinding.CollarActivityCollarBinding
import co.infinum.collar.ui.databinding.CollarViewDetailBinding
import co.infinum.collar.ui.extensions.addBadge
import co.infinum.collar.ui.presentation.decorations.LastDotDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class CollarActivity : AppCompatActivity() {

    companion object {
        private const val FORMAT_DATETIME = "dd.MM.yyyy. HH:mm:ss"
    }

    private var detailDialog: AlertDialog? = null

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
            setupList()
            setupViewModel()
        }

    private fun setupToolbar() {
        with(viewBinding.toolbar) {
            navigationIcon = applicationInfo.loadIcon(packageManager)?.addBadge(this@CollarActivity)
            subtitle = applicationInfo.loadLabel(packageManager)
            setNavigationOnClickListener { finish() }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.search -> {
                        menu.findItem(R.id.filter).isVisible = false
                        menu.findItem(R.id.settings).isVisible = false
                    }
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
                (findViewById(androidx.appcompat.R.id.search_button) as? ImageView)?.setColorFilter(
                    ContextCompat.getColor(context, R.color.collar_color_icon)
                )
                (findViewById(androidx.appcompat.R.id.search_close_btn) as? ImageView)?.setColorFilter(
                    ContextCompat.getColor(context, R.color.collar_color_icon)
                )
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
                setIconifiedByDefault(true)
                isSubmitButtonEnabled = false
                isQueryRefinementEnabled = true
                maxWidth = Integer.MAX_VALUE
                setOnCloseListener {
                    menu.findItem(R.id.filter).isVisible = true
                    menu.findItem(R.id.settings).isVisible = true
                    false
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

    private fun setupList() {
        with(viewBinding.recyclerView) {
            addItemDecoration(LastDotDecoration(context))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = entryAdapter
        }
        viewBinding.emptyLayout.instructionsButton.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .apply {
                        data = Uri.parse(getString(R.string.collar_check_setup_link))
                    }
            )
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CollarViewModel::class.java)
        if (viewModel.entities().hasObservers().not()) {
            viewModel.entities().observe(this) {
                entryAdapter.addItems(it)
                showEmptyView(entryAdapter.itemCount == 0)
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
        detailDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
        super.onDestroy()
    }

    private fun clear() {
        entryAdapter.clear()
        viewModel.clear()
    }

    private fun search(query: String?) = viewModel.search(query)

    @SuppressLint("DefaultLocale")
    @Suppress("ComplexMethod")
    private fun showDetail(entity: CollarEntity) {
        detailDialog = MaterialAlertDialogBuilder(this)
            .setIcon(
                when (entity.type) {
                    EntityType.SCREEN -> R.drawable.collar_ic_screen
                    EntityType.EVENT -> R.drawable.collar_ic_event
                    EntityType.PROPERTY -> R.drawable.collar_ic_property
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
        detailDialog?.show()
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

    private fun showEmptyView(shouldShow: Boolean) {
        with(viewBinding) {
            val isInSearch = toolbar.menu.findItem(R.id.settings).isVisible.not()
            emptyLayout.root.isVisible = shouldShow
            emptyLayout.instructionsButton.isVisible = isInSearch.not()
        }
    }
}
