package co.infinum.collar.ui

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ShareCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import co.infinum.collar.ui.data.room.entity.CollarEntity
import co.infinum.collar.ui.data.room.entity.EntityType
import co.infinum.collar.ui.decorations.LastDotDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.synthetic.main.collar_activity_collar.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CollarActivity : AppCompatActivity(R.layout.collar_activity_collar) {

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, CollarActivity::class.java))
        private const val FORMAT_DATETIME = "dd.MM.yyyy. HH:mm:ss"
    }

    private lateinit var viewModel: CollarViewModel
    private val entryAdapter: CollarAdapter = CollarAdapter(
        onClick = this@CollarActivity::showDetail
    )

    override fun onCreate(savedInstanceState: Bundle?) =
        super.onCreate(savedInstanceState).run {
            setupToolbar()
            setupRecyclerView()
            setupViewModel()
        }

    private fun setupToolbar() {
        with(toolbar) {
            subtitle = applicationInfo.loadLabel(packageManager)
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
                    menu.findItem(R.id.clear).isVisible = hasFocus.not()
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
        with(recyclerView) {
            addItemDecoration(LastDotDecoration(context))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = entryAdapter
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CollarViewModel::class.java)
        if (viewModel.entities().hasObservers().not()) {
            viewModel.entities().observe(this) {
                entryAdapter.addItems(it)
            }
        }
    }

    override fun onDestroy() {
        if (viewModel.entities().hasObservers()) {
            viewModel.entities().removeObservers(this)
        }
        super.onDestroy()
    }

    private fun clear() {
        entryAdapter.clear()
        viewModel.delete()
    }

    private fun search(query: String?) = viewModel.search(query)

    @SuppressLint("DefaultLocale", "InflateParams")
    private fun showDetail(entity: CollarEntity) {
        MaterialAlertDialogBuilder(this)
            .setIcon(
                when (entity.type) {
                    EntityType.SCREEN -> R.drawable.collar_ic_screen_black
                    EntityType.EVENT -> R.drawable.collar_ic_event_black
                    EntityType.PROPERTY -> R.drawable.collar_ic_property_black
                    else -> 0
                }
            )
            .setTitle(entity.type?.name?.toLowerCase()?.capitalize())
            .setView(
                LayoutInflater.from(this).inflate(R.layout.collar_view_detail, null).apply {
                    this.findViewById<MaterialTextView>(R.id.timeView).text = entity.timestamp?.let { SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it)) }
                    this.findViewById<MaterialTextView>(R.id.nameView).text = entity.name
                    this.findViewById<MaterialTextView>(R.id.valueCaptionView).text = when (entity.type) {
                        EntityType.EVENT -> entity.parameters?.let { getString(R.string.collar_parameters) }
                        EntityType.PROPERTY -> getString(R.string.collar_value)
                        else -> null
                    }.also {
                        if (it.isNullOrBlank()) {
                            this.findViewById<MaterialTextView>(R.id.valueView).visibility = View.GONE
                            this.findViewById<MaterialTextView>(R.id.valueCaptionView).visibility = View.GONE
                        } else {
                            this.findViewById<MaterialTextView>(R.id.valueView).visibility = View.VISIBLE
                            this.findViewById<MaterialTextView>(R.id.valueCaptionView).visibility = View.VISIBLE
                            this.findViewById<MaterialTextView>(R.id.valueView).text = when (entity.type) {
                                EntityType.EVENT -> entity.parameters
                                EntityType.PROPERTY -> entity.value
                                else -> null
                            }
                        }
                    }
                }
            )
            .setNeutralButton(R.string.collar_share) { _, _ -> share(entity) }
            .create()
            .show()
    }

    private fun share(entity: CollarEntity) {
        ShareCompat.IntentBuilder.from(this)
            .setChooserTitle(R.string.collar_name)
            .setType("text/plain")
            .setText(
                listOfNotNull(
                    entity.timestamp?.let { "time: ${SimpleDateFormat(FORMAT_DATETIME, Locale.getDefault()).format(Date(it))}" },
                    entity.name?.let { "name: $it" },
                    entity.type?.let { "type: ${it.name.toLowerCase(Locale.getDefault())}" },
                    entity.value?.let { "value: $it" },
                    entity.parameters?.let { "parameters: $it" }
                ).joinToString("\n")
            )
            .startChooser()
    }
}