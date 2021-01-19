package co.infinum.collar.ui.extensions

import androidx.appcompat.app.AlertDialog

@Suppress("RedundantUnitExpression")
internal fun AlertDialog.safeDismiss() =
    if (isShowing) {
        dismiss()
    } else {
        Unit
    }
