package co.infinum.collar.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import co.infinum.collar.ui.decorations.LastDotDecoration
import kotlinx.android.synthetic.main.activity_collar.*

class CollarActivity : AppCompatActivity(R.layout.activity_collar) {

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, CollarActivity::class.java))
    }

    private lateinit var viewModel: CollarViewModel
    private val entryAdapter: CollarAdapter = CollarAdapter()

    override fun onCreate(savedInstanceState: Bundle?) =
        super.onCreate(savedInstanceState).run {
            setupToolbar()
            setupRecyclerView()
            setupViewModel()
        }

    private fun setupToolbar() {
        toolbar.subtitle = applicationInfo.loadLabel(packageManager)
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
        viewModel.screens().observe(this) {
            entryAdapter.addItems(it)
        }
        viewModel.events().observe(this) {
            entryAdapter.addItems(it)
        }
        viewModel.properties().observe(this) {
            entryAdapter.addItems(it)
        }
    }

    override fun onDestroy() {
        viewModel.screens().removeObservers(this)
        viewModel.events().removeObservers(this)
        viewModel.properties().removeObservers(this)
        super.onDestroy()
    }
}