package co.infinum.collar.ui.extensions

import android.view.Menu
import androidx.appcompat.widget.SearchView
import co.infinum.collar.ui.R

internal val Menu.searchView
    get() = findItem(R.id.search)?.actionView as? SearchView
