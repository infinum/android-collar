package com.infinum.collar.ui.extensions

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.infinum.collar.ui.R
import com.infinum.collar.ui.presentation.shared.searchable.SimpleQueryTextListener

internal fun SearchView.setup(
    componentName: ComponentName,
    hint: String? = null,
    onSearchClosed: () -> Unit,
    onQueryTextChanged: (String?) -> Unit
) {
    val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
    setSearchableInfo(searchManager.getSearchableInfo(componentName))

    (findViewById(androidx.appcompat.R.id.search_button) as? ImageView)?.setColorFilter(
        ContextCompat.getColor(context, R.color.collar_color_accent)
    )
    (findViewById(androidx.appcompat.R.id.search_close_btn) as? ImageView)?.setColorFilter(
        ContextCompat.getColor(context, R.color.collar_color_accent)
    )
    setIconifiedByDefault(true)
    isSubmitButtonEnabled = false
    isQueryRefinementEnabled = true
    maxWidth = Integer.MAX_VALUE
    hint?.let { queryHint = it }
    setOnCloseListener {
        onSearchClosed()
        false
    }
    setOnQueryTextListener(
        SimpleQueryTextListener(onQueryTextChanged)
    )
}
