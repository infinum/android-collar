package com.infinum.collar.ui.extensions

import com.infinum.collar.ui.presentation.shared.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal val Date.presentationFormat: String
    get() = SimpleDateFormat(
        Constants.FORMAT_DATETIME,
        Locale.getDefault()
    ).format(this)

internal val Date.presentationItemFormat: String
    get() = SimpleDateFormat(
        Constants.FORMAT_ITEM_DATETIME,
        Locale.getDefault()
    ).format(this)
